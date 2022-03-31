package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.lease.LeaseExtensions;
import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.objects.requests.LeaseByIdRequest;
import helmet.bikelab.apiserver.objects.requests.LeaseExtensionByIdRequest;
import helmet.bikelab.apiserver.objects.responses.LeaseExtensionCheckedResponse;
import helmet.bikelab.apiserver.repositories.LeaseExtensionsRepository;
import helmet.bikelab.apiserver.repositories.LeaseInfoRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.LeasesExtensionWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.dgc.Lease;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LeaseExtensionService extends SessService {

    private final LeaseRepository leaseRepository;
    private final LeasesExtensionWorker leasesExtensionWorker;
    private final LeaseInfoRepository leaseInfoRepository;
    private final LeaseExtensionsRepository leaseExtensionsRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;

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
    public SessionRequest extensionLeaseById(SessionRequest request){
        LeaseExtensionByIdRequest leaseExtensionByIdRequest = map(request.getParam(), LeaseExtensionByIdRequest.class);
        leaseExtensionByIdRequest.checkValidation();
        Leases leaseById = leasesExtensionWorker.checkExtensionEnable(leaseExtensionByIdRequest.getLeaseId());
        leasesExtensionWorker.checkBikeForExtensionByBikeNo(leaseById);
        leasesExtensionWorker.shouldStartDateGreaterThan(leaseById, leaseExtensionByIdRequest.getStartDt());
        setLeaseInfoForExtension(leaseById, leaseExtensionByIdRequest.getStartDt(), leaseExtensionByIdRequest.getEndDate(), leaseExtensionByIdRequest.getPeriod());
        LeaseExtensions leaseExtension = getLeaseExtensionList(leaseById, leaseExtensionByIdRequest.getStartDt(), leaseExtensionByIdRequest.getEndDate(), leaseExtensionByIdRequest.getPeriod());
        leaseExtensionsRepository.save(leaseExtension);

        List<LeasePayments> leasePayments = getLeasePaymentList(leaseById, leaseExtensionByIdRequest.getPeriod());
        if(bePresent(leasePayments)) leasePaymentsRepository.saveAll(leasePayments);

        leaseById.setExtensionLease();
        leaseRepository.save(leaseById);
        return request;
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

    private List<LeasePayments> getLeasePaymentList(Leases leases, Integer period){
        return Stream.iterate(period, n -> n).map(e -> {
            LeasePayments leasePayments = new LeasePayments();
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
