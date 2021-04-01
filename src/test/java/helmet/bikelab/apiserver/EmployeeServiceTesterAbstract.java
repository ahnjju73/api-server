package helmet.bikelab.apiserver;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserSession;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

@SpringBootTest
public class EmployeeServiceTesterAbstract {

    private static BikeUser bikeUser;

    @Bean(name = "bike_user")
    public BikeUser getBikeUser(){
        if(bikeUser==null){
            bikeUser = new BikeUser();
        }
        return bikeUser;
    }

    @Before
    public void initialize(){

    }
}
