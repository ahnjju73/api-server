package helmet.bikelab.apiserver.schedulers;

import helmet.bikelab.apiserver.schedulers.internal.AbstractSchedulers;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class ASchedulerComponent extends AbstractSchedulers {

    private final Scheduler scheduler;

    @PostConstruct
    public void start() throws SchedulerException {
        JobDetail leaseFinishSchedulerService = buildJobDetail(LeaseFinishSchedulerService.class, "LeaseFinishSchedulerService", "", new HashMap());
        doScheduler(leaseFinishSchedulerService, "0 0 1 * * ?");

        JobDetail unPaidLeaseSchedulerService = buildJobDetail(UnPaidLeaseSchedulerService.class, "UnPaidLeaseSchedulerService", "", new HashMap());
//        doScheduler(unPaidLeaseSchedulerService, "0 0 9 * * ?");

        JobDetail summarySchedulerService = buildJobDetail(SummarySchedulerService.class, "SummarySchedulerService", "", new HashMap());
        doScheduler(summarySchedulerService, "0 0 9 * * ?");
    }

    private void doScheduler(JobDetail jobDetail, String cronTab) throws SchedulerException {
        if(bePresent(jobDetail)) scheduler.scheduleJob(jobDetail, buildCronJobTrigger(cronTab));
    }

}
