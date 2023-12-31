package helmet.bikelab.apiserver.schedulers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeStatusTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;
import helmet.bikelab.apiserver.repositories.BikesRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.schedulers.internal.WorkspaceQuartz;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeaseFinishSchedulerService extends WorkspaceQuartz {

    private final LeaseRepository leaseRepository;
    private final BikeWorker bikeWorker;
    private final LeasesWorker leasesWorker;
    private final BikesRepository bikesRepository;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        List<Map> leases = getList("quartz.leases-stop.fetchAllLeasesByConfirmedAndContinue", null);
        if(bePresent(leases)){
            Bikes emptyBikes = bikeWorker.getEmptyBikes();
            leases.forEach(elm -> {
                String leaseId = (String)elm.get("lease_id");
                Leases leaseByLeaseId = leasesWorker.getLeaseByLeaseId(leaseId);
                LeaseInfo leaseInfo = leaseByLeaseId.getLeaseInfo();
                if(leaseInfo.getEndDate().isBefore(LocalDate.now()) ){
                    Bikes bike = leaseByLeaseId.getBike();
                    Clients client = leaseByLeaseId.getClients();
                    bike.doDeclineRider();
                    leaseByLeaseId.setLeaseStopStatus(LeaseStopStatusTypes.FINISH);
                    leaseByLeaseId.setBikeNo(emptyBikes.getBikeNo());
                    leaseByLeaseId.setStopDt(LocalDateTime.now());
                    leaseByLeaseId.setStopReason("계약만료");
                    leaseRepository.save(leaseByLeaseId);
                    // 만료가 될 경우, 차량 보관상태는 '보관중'으로 변경된다.
                    bike.setBikeStatus(BikeStatusTypes.PENDING);
                    try{
                        bike.setWarehouse(client.getClientInfo().getName());
                    }catch (Exception e){
                        bike.setWarehouse("리스 만료");
                    }
                    bikesRepository.save(bike);
                }
            });
        }
    }

}
