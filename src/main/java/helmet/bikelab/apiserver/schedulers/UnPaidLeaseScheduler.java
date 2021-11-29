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
public class UnPaidLeaseScheduler extends AbstractSchedulers {

    private final Scheduler scheduler;

    @PostConstruct
    public void start() throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(UnPaidLeaseSchedulerService.class, "UnPaidLeaseSchedulerService", "", new HashMap());
        if(bePresent(jobDetail)) scheduler.scheduleJob(jobDetail, buildCronJobTrigger("0 0 9 * * ?"));
    }

}
