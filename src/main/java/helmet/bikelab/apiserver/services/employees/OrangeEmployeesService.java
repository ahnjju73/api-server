package helmet.bikelab.apiserver.services.employees;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserInfo;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserPassword;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.employees.AddEmployeeRequest;
import helmet.bikelab.apiserver.repositories.BikeLabUserInfoRepository;
import helmet.bikelab.apiserver.repositories.BikeLabUserPasswordRepository;
import helmet.bikelab.apiserver.repositories.BikeLabUserRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrangeEmployeesService extends SessService {

    private final BikeLabUserInfoRepository userInfoRepository;
    private final BikeLabUserRepository userRepository;
    private final BikeLabUserPasswordRepository userPasswordRepository;
    private final AutoKey autoKey;

    @Transactional
    public BikeSessionRequest fetchListOfEmployee(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        response.put("employees", getList("", param));
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addEmployee(BikeSessionRequest request){
        Map param = request.getParam();
        AddEmployeeRequest addEmployeeRequest = map(param, AddEmployeeRequest.class);
        addEmployeeRequest.validation();
        String userId = autoKey.makeGetKey("bike_user");

        BikeUser newUser = new BikeUser();
        newUser.setUserId(userId);
        newUser.setEmail(addEmployeeRequest.getEmail());
        newUser.setUserStatusTypes(BikeUserStatusTypes.COMPLETED);
        userRepository.save(newUser);


        BikeUserInfo bikeUserInfo = new BikeUserInfo();
        bikeUserInfo.setBikeUser(newUser);
        userInfoRepository.save(bikeUserInfo);

        BikeUserPassword password = new BikeUserPassword();
        password.newPassword(newUser.getEmail());
        userPasswordRepository.save(password);
        return  request;
    }


    public BikeSessionRequest deleteEmployee(BikeSessionRequest request) {
        Map param = request.getParam();
        String userId = (String)param.get("user_id");
        userRepository.deleteByUserId(userId);

        return request;
    }

    public BikeSessionRequest updateEmployee(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        String name = (String)param.get("name");
        String password = (String)param.get("pass_word");
        String phoneNum = (String)param.get("phone_num");
        String description = (String)param.get("description");




        return request;
    }
}
