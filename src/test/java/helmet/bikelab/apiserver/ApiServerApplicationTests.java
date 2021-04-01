package helmet.bikelab.apiserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserInfo;
import helmet.bikelab.apiserver.domain.client.ClientGroups;
import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.repositories.ClientsRepository;
import helmet.bikelab.apiserver.services.myinfo.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertThat;

@SpringBootTest
class ApiServerApplicationTests extends AbstractTester{

    @Autowired
    private ProfileService profileService;

    @Test
    public void 유저회원가입(){
        ObjectMapper objectMapper = new ObjectMapper();

        BikeSessionRequest bikeSessionRequest = new BikeSessionRequest();
        bikeSessionRequest.setSessionUser(getBikeUser());
        BikeUserInfo bikeUserInfo = new BikeUserInfo();
        bikeUserInfo.setName("asdlkjdlkjfdd");
        bikeSessionRequest.setParam(objectMapper.convertValue(bikeUserInfo, HashMap.class));
        BikeSessionRequest bikeSessionRequest1 = profileService.modifyProfile(bikeSessionRequest);

    }
}