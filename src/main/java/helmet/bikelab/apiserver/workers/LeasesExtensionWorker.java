package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.domain.lease.LeasePrice;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;
import helmet.bikelab.apiserver.domain.types.PaymentTypes;
import helmet.bikelab.apiserver.repositories.BikesRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class LeasesExtensionWorker extends Workspace {

    private final LeaseRepository leaseRepository;
    private final BikeWorker bikeWorker;
    private final LeasesWorker leasesWorker;

    /**
     * 계약서 연장가능 여부 확인하기
     * 여부 조건.
     * 1. 결제완료된 계약서
     * 2. 리스 또는 렌트 형태의 계약서
     * 3. 월차감 형태의 계약서만 가능하다.
     * 4. 중도해지된 계약서의 경우, 연장불가능하다.
     * @param leaseId
     * @return Leases
     */
    public Leases checkExtensionEnable(String leaseId){
        Leases leaseById = leasesWorker.getLeaseByLeaseId(leaseId);
        LeasePrice leasePrice = leaseById.getLeasePrice();
        if(LeaseStopStatusTypes.STOP_CONTINUE.equals(leaseById.getLeaseStopStatus()) || LeaseStopStatusTypes.ETC.equals(leaseById.getLeaseStopStatus()))
            withException("852-005");
        if(!LeaseStatusTypes.CONFIRM.equals(leaseById.getStatus()))
            withException("852-001");
        if(!ContractTypes.LEASE.equals(leaseById.getContractTypes()) && !ContractTypes.MANAGEMENT.equals(leaseById.getContractTypes()))
            withException("852-002");
        if(!PaymentTypes.MONTHLY.equals(leasePrice.getType())) withException("852-003");
        return leaseById;
    }

    /**
     * 계역서 연장하기 위한 시작일자 검증.
     * 1. start_dt 가 lease_info의 end_dt 보다 커야 한다.
     * 2. period 가 1개월 이상이어야 한다.
     * @param leases
     * @param startDate
     * @return
     */
    public Leases shouldStartDateGreaterThan(Leases leases, LocalDate startDate){
        LeaseInfo leaseInfo = leases.getLeaseInfo();
        LocalDate endDate = leaseInfo.getEndDate();
        if(endDate.isAfter(startDate) || endDate.isEqual(startDate)) withException("852-004");
        return leases;
    }

    public Bikes checkBikeForExtensionByBikeNo(Leases leases){
        Bikes emptyBikes = bikeWorker.getEmptyBikes();
        Bikes leaseBike = leases.getBike();
        if(leases.getBakBikeNo().equals(leases.getBikeNo())) {
            return leases.getBike();
        }else {
//        }else if(bePresent(leaseBike) && emptyBikes.getBikeNo().equals(leaseBike)){
            Leases leaseByBakBikeNo = leaseRepository.findByBikeNo(leases.getBakBikeNo());
            if(bePresent(leaseByBakBikeNo) && !leases.getLeaseId().equals(leaseByBakBikeNo.getLeaseId()))
                writeMessage("이미 계약서에 등록된 차량정보 입니다. 리스번호 [ " + leaseByBakBikeNo.getLeaseId() + " ]");
            Bikes bikeByNo = bikeWorker.getBikeByNo(leases.getBakBikeNo());
            return bikeByNo;
        }
    }

}
