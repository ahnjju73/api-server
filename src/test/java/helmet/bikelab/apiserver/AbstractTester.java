package helmet.bikelab.apiserver;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.services.internal.Workspace;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AbstractTester extends Workspace {

    private static BikeUser bikeUser;

    @Before
    public void initialized(){

        if(bikeUser == null){
        }

    }

    public static BikeUser getBikeUser() {
        return bikeUser;
    }

    public static void setBikeUser(BikeUser bikeUser) {
        AbstractTester.bikeUser = bikeUser;
    }
}
