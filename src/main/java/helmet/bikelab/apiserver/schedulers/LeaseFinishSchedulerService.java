package helmet.bikelab.apiserver.schedulers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.schedulers.internal.WorkspaceQuartz;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeaseFinishSchedulerService extends WorkspaceQuartz {

    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final BikeWorker bikeWorker;
    private final LeasesWorker leasesWorker;

    @Override
    @Transactional
    protected void executeInternal(JobExecutionContext context) {
        List<Map> leases = getList("quartz.leases-stop.fetchAllLeasesByConfirmedAndContinue", null);
        if(bePresent(leases)){
            Bikes emptyBikes = bikeWorker.getEmptyBikes();
            leases.forEach(elm -> {
                String leaseId = (String)elm.get("lease_id");
                Leases leaseByLeaseId = leasesWorker.getLeaseByLeaseId(leaseId);
                List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseIdOrderByIndex(leaseId);
                if(payments.size() > 0){
                    LeasePayments leasePayments = payments.get(payments.size() - 1);
                    LocalDate paymentDate = leasePayments.getPaymentDate();
                    if(paymentDate.isBefore(LocalDate.now()) ){
                        Bikes bike = leaseByLeaseId.getBike();
                        bike.doDeclineRider();
                        leaseByLeaseId.setLeaseStopStatus(LeaseStopStatusTypes.FINSISH);
                        leaseByLeaseId.setBikeNo(emptyBikes.getBikeNo());
                        leaseByLeaseId.setStopDt(LocalDateTime.now());
                        leaseByLeaseId.setStopReason("계약만료");
                        leaseRepository.save(leaseByLeaseId);
                    }
                }
            });
        }
    }

}
