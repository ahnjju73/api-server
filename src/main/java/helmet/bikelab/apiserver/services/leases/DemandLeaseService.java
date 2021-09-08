package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
import helmet.bikelab.apiserver.domain.types.DemandLeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.PaymentTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
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

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class DemandLeaseService extends SessService {

    private final DemandLeaseWorker demandLeaseWorker;
    private final AutoKey autoKey;
    private final LeasesWorker leasesWorker;
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeasePriceRepository leasePriceRepository;
    private final BikesRepository bikesRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final InsurancesRepository insurancesRepository;

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
//        demandLeaseByIdRequest.checkValidation();
        demandLeaseById.setRejectMessage(null);
        demandLeaseById.setCompletedAt(LocalDateTime.now());
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
            lease.setCreatedUserNo(request.getSessionUser().getUserNo());
            leaseRepository.save(lease);

            demandLeaseWorker.updateDemandLeaseStatusByDemandLease(demandLeaseById, DemandLeaseStatusTypes.COMPLETED);

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
            leasePriceRepository.save(leasePrice);
            Integer leaseFee = 0;
            List<LeasePayments> leasePaymentsList = new ArrayList<>();
            BikeUser sessionUser = request.getSessionUser();
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

        return request;
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
