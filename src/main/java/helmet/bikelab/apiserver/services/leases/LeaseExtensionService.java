package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.lease.LeaseExtensions;
import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.objects.requests.LeaseByIdRequest;
import helmet.bikelab.apiserver.objects.requests.LeaseExtensionByIdRequest;
import helmet.bikelab.apiserver.objects.responses.LeaseExtensionCheckedResponse;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.workers.LeasesExtensionWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class LeaseExtensionService extends SessService {

    private final LeaseRepository leaseRepository;
    private final LeasesExtensionWorker leasesExtensionWorker;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeaseExtensionsRepository leaseExtensionsRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final AutoKey autoKey;
    private final BikeUserLogRepository bikeUserLogRepository;

    public SessionRequest getLeaseExtensionList(SessionRequest request){
        LeaseByIdRequest leaseByIdRequest = map(request.getParam(), LeaseByIdRequest.class);
        List<LeaseExtensions> leaseExtensions = leaseExtensionsRepository.findByLease_LeaseIdOrderByIdx(leaseByIdRequest.getLeaseId());
        request.setResponse(leaseExtensions);
        return request;
    }

    public SessionRequest checkIfExtension(SessionRequest request){
        LeaseByIdRequest leaseByIdRequest = map(request.getParam(), LeaseByIdRequest.class);
        Leases leases = leasesExtensionWorker.checkExtensionEnable(leaseByIdRequest.getLeaseId());
        Bikes bikes = leasesExtensionWorker.checkBikeForExtensionByBikeNo(leases);
        LeaseExtensionCheckedResponse response = new LeaseExtensionCheckedResponse();
        response.setBike(bikes);
        request.setResponse(response);
        return request;
    }

    /**
     * Reuqest validation
     *  1. checkIfExtension();
     *  2. start_dt 가 lease_info의 end_dt 보다 커야 한다.
     *  3. period 가 1개월 이상이어야 한다.
     * @param request
     * @return
     */
    @Transactional
    public SessionRequest extensionLeaseById(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        LeaseExtensionByIdRequest leaseExtensionByIdRequest = map(request.getParam(), LeaseExtensionByIdRequest.class);
        leaseExtensionByIdRequest.checkValidation();
        Leases leaseById = leasesExtensionWorker.checkExtensionEnable(leaseExtensionByIdRequest.getLeaseId());
        leasesExtensionWorker.checkBikeForExtensionByBikeNo(leaseById);
        leasesExtensionWorker.shouldStartDateGreaterThan(leaseById, leaseExtensionByIdRequest.getStartDt());
        LeaseExtensions leaseExtension = getLeaseExtensionList(leaseById, leaseExtensionByIdRequest.getStartDt(), leaseExtensionByIdRequest.getEndDateByLeaseInfo(), leaseExtensionByIdRequest.getPeriod());
        leaseExtensionsRepository.save(leaseExtension);

        setLeaseInfoForExtension(leaseById, leaseExtensionByIdRequest.getStartDt(), leaseExtensionByIdRequest.getEndDate(), leaseExtensionByIdRequest.getPeriod());
        leaseById.setExtensionLease();
        leaseRepository.save(leaseById);

        List<LeasePayments> leasePayments = getLeasePaymentList(leaseById, leaseExtensionByIdRequest.getStartDt(), leaseExtensionByIdRequest.getPeriod(), sessionUser);
        if(bePresent(leasePayments)) leasePaymentsRepository.saveAll(leasePayments);
        updateLeaseLogByExtension(leaseById, leaseExtension, sessionUser);
        return request;
    }

    private void updateLeaseLogByExtension(Leases lease, LeaseExtensions leaseExtensions, BikeUser session){
        List<String> logList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        Integer extensionIndexByLeaseNo = leaseExtensionsRepository.getExtensionIndexByLeaseNo(lease.getLeaseNo());
        if(!bePresent(extensionIndexByLeaseNo)) extensionIndexByLeaseNo = 0;
        extensionIndexByLeaseNo++;
        logList.add("<>[" + extensionIndexByLeaseNo + "회차]</> 리스가 연장되었습니다.\n");
        logList.add("시작일 : <>" + leaseExtensions.getStart().format(formatter) + "</>\n");
        logList.add("종료일 : <>" + leaseExtensions.getEndDate().format(formatter) + "</>\n");
        logList.add("기간 : <>" + leaseExtensions.getPeriod() + "개월</>\n");
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, session.getUserNo(), lease.getLeaseNo().toString(), logList));
    }

    private LeaseExtensions getLeaseExtensionList(Leases leases, LocalDate startDate, LocalDate endDate, Integer period){
        LeaseExtensions leaseExtensions = new LeaseExtensions();
        leaseExtensions.setLeaseNo(leases.getLeaseNo());
        leaseExtensions.setStart(startDate);
        leaseExtensions.setEndDate(endDate);
        leaseExtensions.setPeriod(period);
        leaseExtensions.setLeaseStopStatus(leases.getLeaseStopStatus());
        leaseExtensions.setStopDt(leases.getStopDt());
        leaseExtensions.setStopFee(leases.getStopFee());
        leaseExtensions.setStopPaidFee(leases.getStopPaidFee());
        leaseExtensions.setStopReason(leases.getStopReason());
        return leaseExtensions;
    }

    private List<LeasePayments> getLeasePaymentList(Leases leases, LocalDate startDate, Integer period, BikeUser session){
        LeasePayments lastIndexByLeaseNoOrderByIndexDesc = leasePaymentsRepository.getLastIndexByLeaseNoOrderByIndexDesc(leases.getLeaseNo());
        Bikes bike = leases.getBike();
        Integer lastIndex = lastIndexByLeaseNoOrderByIndexDesc.getIndex();
        lastIndex++;
        AtomicReference<Integer> finalLastIndex = new AtomicReference<>(lastIndex);
        return Stream.iterate(0, n -> n + 1)
                .limit(period).map(e -> {
            String paymentId = autoKey.makeGetKey("payment");
            LeasePayments leasePayments = new LeasePayments();
            leasePayments.setLeaseNo(leases.getLeaseNo());
            leasePayments.setPaymentId(paymentId);
            leasePayments.setClientNo(leases.getClientNo());
            leasePayments.setIndex(finalLastIndex.getAndSet(finalLastIndex.get() + 1));
            leasePayments.setLeaseFee(lastIndexByLeaseNoOrderByIndexDesc.getLeaseFee());
            leasePayments.setInsertedUserNo(session.getUserNo());
            leasePayments.setBikeNo(bike.getBikeNo());
            leasePayments.setRiderNo(bike.getRiderNo());
            leasePayments.setPaymentDate(startDate.plusMonths(e));
            return leasePayments;
        }).collect(Collectors.toList());
    }

    private LeaseInfo setLeaseInfoForExtension(Leases leases, LocalDate startDate, LocalDate endDate, Integer period){
        LeaseInfo leaseInfo = leases.getLeaseInfo();
        leaseInfo.setStart(startDate);
        leaseInfo.setEndDate(endDate);
        leaseInfo.setPeriod(period);
        leaseInfoRepository.save(leaseInfo);
        return leaseInfo;
    }

}
