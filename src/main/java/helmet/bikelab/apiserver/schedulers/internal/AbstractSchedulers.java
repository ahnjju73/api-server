package helmet.bikelab.apiserver.schedulers.internal;

import helmet.bikelab.apiserver.services.internal.OriginObject;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

public abstract class AbstractSchedulers extends OriginObject {

    @Value("${application.scheduler.on}")
    public Boolean schedulerOn;

    public Trigger buildCronJobTrigger(String scheduleExp) {
        return TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp))
                .build();
    }

    public JobDetail buildJobDetail(Class job, String name, String desc, Map params) {
        if(!schedulerOn) return null;
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);
        return JobBuilder
                .newJob(job)
                .withIdentity(name)
                .withDescription(desc)
                .usingJobData(jobDataMap)
                .build();
    }

}
