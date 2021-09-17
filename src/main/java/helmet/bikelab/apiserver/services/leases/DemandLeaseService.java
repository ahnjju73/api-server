package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.demands.DemandLeaseAttachments;
import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PageableResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeaseListByDemandLeaseIdRequest;
import helmet.bikelab.apiserver.objects.requests.DemandLeaseByIdRequest;
import helmet.bikelab.apiserver.objects.requests.RejectDemandLeaseByIdRequest;
import helmet.bikelab.apiserver.objects.responses.DemandLeaseDetailsByIdResponse;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.workers.DemandLeaseWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class DemandLeaseService extends SessService {

    private final DemandLeaseWorker demandLeaseWorker;
    private final AutoKey autoKey;
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeasePriceRepository leasePriceRepository;
    private final BikesRepository bikesRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final DemandLeaseAttachmentsRepository demandLeaseAttachmentsRepository;
    private final DemandLeasesRepository demandLeasesRepository;
    private final ExecutorService executorService;

    public BikeSessionRequest fetchAttachmentsByDemandLeaseId(BikeSessionRequest request){
        DemandLeaseByIdRequest demandLeaseByIdRequest = map(request.getParam(), DemandLeaseByIdRequest.class);
        List<DemandLeaseAttachments> demandLeaseAttachments = demandLeaseAttachmentsRepository.findAllByDemandLeases_DemandLeaseId(demandLeaseByIdRequest.getDemandLeaseId());
        request.setResponse(demandLeaseAttachments == null ? new ArrayList<>() : demandLeaseAttachments);
        return request;
    }

    public BikeSessionRequest fetchLeaseListByDemandLeaseNo(BikeSessionRequest request){
        LeaseListByDemandLeaseIdRequest demandLeaseByIdRequest = map(request.getParam(), LeaseListByDemandLeaseIdRequest.class);
        PageableResponse pageableResponse = new PageableResponse();
        DemandLeases demandLeaseById = demandLeaseWorker.getDemandLeaseById(demandLeaseByIdRequest.getDemandLeaseId());
        List<Leases> leasesByDemandLeaseId = demandLeaseWorker.getLeasesByDemandLeaseId(demandLeaseByIdRequest.getDemandLeaseId(), demandLeaseByIdRequest);
        List<String> collect = leasesByDemandLeaseId.stream().map(row -> row.getLeaseId()).collect(Collectors.toList());
        pageableResponse.setList(collect);
        pageableResponse.setTotal(demandLeaseById.getAmounts());
        pageableResponse.setPage(!bePresent(demandLeaseWorker) ? demandLeaseByIdRequest.getPage() : demandLeaseByIdRequest.getPage() + 1);
        pageableResponse.setSize(demandLeaseByIdRequest.getSize());
        request.setResponse(pageableResponse);
        return request;
    }

    public BikeSessionRequest fetchDemandLeaseById(BikeSessionRequest request){
        DemandLeaseByIdRequest demandLeaseByIdRequest = map(request.getParam(), DemandLeaseByIdRequest.class);
        DemandLeases demandLeases = demandLeaseWorker.getDemandLeaseById(demandLeaseByIdRequest.getDemandLeaseId());

        DemandLeaseDetailsByIdResponse demandLeaseDetailsByIdResponse = new DemandLeaseDetailsByIdResponse();
        demandLeaseDetailsByIdResponse.setDemandLease(demandLeases);
        demandLeaseDetailsByIdResponse.setClient(demandLeases.getClient());
        try {
//            Leases leases = leasesWorker.getLeaseByLeaseNo(demandLeases.getLeaseNo());
//            if(bePresent(leases)) demandLeaseDetailsByIdResponse.setLeaseId(leases.getLeaseId());
        }catch (Exception e){ }
        request.setResponse(demandLeaseDetailsByIdResponse);
        return request;
    }

    @Transactional
    public BikeSessionRequest completedDemandLeaseById(BikeSessionRequest request){
        Map param = request.getParam();
        DemandLeaseByIdRequest demandLeaseByIdRequest = map(request.getParam(), DemandLeaseByIdRequest.class);
        DemandLeases demandLeaseById = demandLeaseWorker.getDemandLeaseById(demandLeaseByIdRequest.getDemandLeaseId());
        if(!demandLeaseById.isOneOfDemandLeaseStatusType(DemandLeaseStatusTypes.PENDING)) withException("803-002");
        if(DemandLeaseContractTypes.PENDING.equals(demandLeaseById.getContracted())
                || DemandLeaseContractTypes.FAILED.equals(demandLeaseById.getContracted())) {
            executorService.submit(() -> {
                try {
                    createLeaseContractsFromDemandLeases(demandLeaseById, request.getSessionUser(), param);
                }catch (Exception e){
                    demandLeaseById.setFailContracted(e.getMessage());
                    demandLeaseById.setContracted(DemandLeaseContractTypes.FAILED);
                    demandLeasesRepository.save(demandLeaseById);
                }
            });
            demandLeaseById.setContracted(DemandLeaseContractTypes.CONTRACTING);
            demandLeaseById.setFailContracted(null);
            demandLeaseById.setRejectMessage(null);
            demandLeaseById.setCompletedAt(LocalDateTime.now());
            demandLeasesRepository.save(demandLeaseById);
        }else {
            withException("803-008");
        }

        return request;
    }

    @Transactional
    public void createLeaseContractsFromDemandLeases(DemandLeases demandLeaseById, BikeUser sessionUser, Map param){
        String bikeId = (String)getItem("comm.common.getEmptyCar", param);
        Bikes bike = bikesRepository.findByBikeId(bikeId);
        String insuranceNo = (String)getItem("comm.common.getDefaultInsurance", param);
        Integer cycleToLease = demandLeaseById.getAmounts();
        for(int size = 0; size < cycleToLease; size++){
            Leases lease = new Leases();
            String leaseId = autoKey.makeGetKey("lease");
            lease.setLeaseId(leaseId);
            lease.setInsuranceNo(Integer.parseInt(insuranceNo));
            lease.setClientNo(demandLeaseById.getClientNo());
            lease.setBikeNo(bike.getBikeNo());
            lease.setType(demandLeaseById.getManagementType());
            lease.setContractTypes(ContractTypes.OPERATING);
            lease.setCreatedAt(LocalDateTime.now());
            lease.setReleaseNo(1);
            lease.setDemandLeaseNo(demandLeaseById.getDemandLeaseNo());
            lease.setCreatedUserNo(sessionUser.getUserNo());
            leaseRepository.save(lease);

            LeaseInfo leaseInfo = new LeaseInfo();
            leaseInfo.setLeaseNo(lease.getLeaseNo());
            leaseInfo.setPeriod(demandLeaseById.getPeriod());
            leaseInfo.setStart(LocalDate.now());
            leaseInfo.setEndDate(leaseInfo.getStart().plusMonths(leaseInfo.getPeriod()));
            leaseInfo.setContractDate(LocalDate.now());
            leaseInfoRepository.save(leaseInfo);

            LeasePrice leasePrice = new LeasePrice();
            leasePrice.setLeaseNo(lease.getLeaseNo());
            leasePrice.setType(demandLeaseById.getPaymentType());
            leasePrice.setPrepayment(demandLeaseById.getPrepayment());
            leasePriceRepository.save(leasePrice);
            Integer leaseFee = 0;
            List<LeasePayments> leasePaymentsList = new ArrayList<>();
            if(leasePrice.getType().equals(PaymentTypes.MONTHLY)) {
                for (int i = 0; i < leaseInfo.getPeriod(); i++) {
                    LeasePayments leasePayment = new LeasePayments();
                    String paymentId = autoKey.makeGetKey("payment");
                    leasePayment.setPaymentId(paymentId);
                    leasePayment.setLeaseNo(lease.getLeaseNo());
                    leasePayment.setIndex(i + 1);
                    leasePayment.setPaymentDate(leaseInfo.getStart().plusMonths(i));
                    leasePayment.setInsertedUserNo(sessionUser.getUserNo());
                    leasePayment.setLeaseFee(leaseFee);
                    leasePaymentsList.add(leasePayment);
                }
            }else{
                int days = (int)(ChronoUnit.DAYS.between(leaseInfo.getStart(), leaseInfo.getEndDate()));
                for(int i = 0 ; i < days; i++){
                    LeasePayments leasePayment = new LeasePayments();
                    String paymentId = autoKey.makeGetKey("payment");
                    leasePayment.setPaymentId(paymentId);
                    leasePayment.setLeaseNo(lease.getLeaseNo());
                    leasePayment.setIndex(i + 1);
                    leasePayment.setPaymentDate(leaseInfo.getStart().plusDays(i));
                    leasePayment.setInsertedUserNo(sessionUser.getUserNo());
                    leasePayment.setLeaseFee(leaseFee);
                    leasePaymentsList.add(leasePayment);
                }
            }
            leasePaymentsRepository.saveAll(leasePaymentsList);
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_ADDED, sessionUser.getUserNo(), lease.getLeaseNo().toString()));
        }
        demandLeaseById.setContracted(DemandLeaseContractTypes.CONTRACTED);
        demandLeaseById.setRejectMessage(null);
        demandLeaseById.setCompletedAt(LocalDateTime.now());
        demandLeasesRepository.save(demandLeaseById);
        demandLeaseWorker.updateDemandLeaseStatusByDemandLease(demandLeaseById, DemandLeaseStatusTypes.COMPLETED);
    }

    @Transactional
    public BikeSessionRequest denyDemandLeaseById(BikeSessionRequest request){
        RejectDemandLeaseByIdRequest demandLeaseByIdRequest = map(request.getParam(), RejectDemandLeaseByIdRequest.class);
        DemandLeases demandLeaseById = demandLeaseWorker.getDemandLeaseById(demandLeaseByIdRequest.getDemandLeaseId());
        demandLeaseById.setRejectMessage(demandLeaseByIdRequest.getRejectedMessage());
        demandLeaseWorker.updateDemandLeaseStatusByDemandLease(demandLeaseById, DemandLeaseStatusTypes.DENIED);
        return request;
    }

}
