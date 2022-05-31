package helmet.bikelab.apiserver;

import helmet.bikelab.apiserver.schedulers.SummarySchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiServerApplication {

    @Autowired
    private SummarySchedulerService summarySchedulerService;

    public static void main(String[] args) {
        SpringApplication.run(ApiServerApplication.class, args);
    }

}
