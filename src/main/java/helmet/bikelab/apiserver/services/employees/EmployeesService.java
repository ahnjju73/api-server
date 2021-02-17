package helmet.bikelab.apiserver.services.employees;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserPassword;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.repositories.BikeLabUserInfoRepository;
import helmet.bikelab.apiserver.repositories.BikeLabUserPasswordRepository;
import helmet.bikelab.apiserver.repositories.BikeLabUserRepository;
import helmet.bikelab.apiserver.services.internal.BikeSessionService;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmployeesService extends SessService {

    @Autowired
    private BikeLabUserInfoRepository adminUserInfoRepository;

    @Autowired
    private BikeLabUserRepository userRepository;

    @Autowired
    private BikeLabUserPasswordRepository userPasswordRepository;

    public BikeSessionService fetchListOfEmployee(BikeSessionService request){
        Map param = request.getParam();
        Map response = new HashMap();
        response.put("employees", getList("mrmention.employee.fetchListOfEmployee", param));
        request.setResponse(response);
        return request;
    }

    public BikeSessionService fetchMyInfo(BikeSessionService request){
        Map response = new HashMap();
        BikeUser sessionUser = request.getSessionUser();
        adminUserInfoRepository
                .findByBikeUserNo(sessionUser.getUserNo())
                .ifPresent(userInfo -> {
                    response.put("user_no", sessionUser.getUserNo());
                    response.put("email", sessionUser.getEmail());
//                    response.put("username", userInfo.getUserName());
//                    response.put("intro", userInfo.getIntroduction());
//                    response.put("phone", userInfo.getPhone());
                });
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionService updateMyInfo(BikeSessionService request){
        updateUserInformation(request, request.getSessionUser().getUserNo());
        return request;
    }

    private void updateUserInformation(BikeSessionService request, Integer userNo){
        Map param = request.getParam();
        Map response = new HashMap();
        String username = (String)param.get("username");
        String email = (String)param.get("email");
        String password = (String)param.get("password");
        String confirmPassword = (String)param.get("confirm_password");
        String phone = (String)param.get("phone");
        String intro = (String)param.get("intro");
        adminUserInfoRepository
                .findByBikeUserNo(userNo)
                .ifPresent(userInfo -> {
                    BikeUser adminUser = userInfo.getBikeUser();
                    if(bePresent(username)){
//                        userInfo.setUserName(username);
                    }
                    if(bePresent(email)){
                        userRepository
                                .findByEmailAndUserNoNot(email, adminUser.getUserNo())
                                .ifPresentOrElse(user -> writeError(param, "801-001"), () -> {
                                    adminUser.setEmail(email);
                                });
                    }
                    updatePasswordByUserNo(password, confirmPassword, userNo);

                    if(bePresent(phone)){
//                        userInfo.setPhone(phone);
                    }
                    if(bePresent(intro)){
//                        userInfo.setIntroduction(intro);
                    }
                    adminUserInfoRepository.save(userInfo);
                    userRepository.save(adminUser);

//                    response.put("username", userInfo.getUserName());
                    response.put("email", adminUser.getEmail());
//                    response.put("phone", userInfo.getPhone());
//                    response.put("intro", userInfo.getIntroduction());
                });
    }

    private void updatePasswordByUserNo(String password, String confirmPassword, Integer userNo){
        String blankPassword = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        if(bePresent(password) && bePresent(confirmPassword) &&
                !blankPassword.equals(password) && !blankPassword.equals(confirmPassword) &&
                (bePresent(password) == bePresent(confirmPassword))){
            String salt = Crypt.newCrypt().getSalt(128);
            String cryptedPassword = Crypt.newCrypt().getPassword(password, salt);
            userPasswordRepository
                    .findByBikeUserNo(userNo)
                    .ifPresentOrElse(userPassword -> {
                        userPassword.setPassword(cryptedPassword);
                        userPassword.setSalt(salt);
                        userPasswordRepository.save(userPassword);
                    }, () -> {
                        BikeUserPassword userPassword = new BikeUserPassword();
                        userPassword.setPassword(cryptedPassword);
                        userPassword.setSalt(salt);
                        userPassword.setBikeUserNo(userNo);
                        userPasswordRepository.save(userPassword);
                    });
        }
    }

    @Transactional
    public BikeSessionService modifyEmployeeInfoByManager(BikeSessionService request){
        Map param = request.getParam();
        Integer userNo = Integer.parseInt((String)param.get("user_no"));
        updateUserInformation(request, userNo);
        return request;
    }

    @Transactional
    public BikeSessionService fireEmployee(BikeSessionService request){
        Map param = request.getParam();
        String userNo = (String)param.get("user_no");
        userRepository.updateAccountStatusOfEmployeeByManager(userNo, BikeUserStatusTypes.DELETED);
        return request;
    }

    public BikeSessionService fetchEmployeeInfoByUserNo(BikeSessionService request){
        Map param = request.getParam();
        request.setResponse(getItem("mrmention.employee.fetchListOfEmployeeByUserNo", param));
        return request;
    }

}
