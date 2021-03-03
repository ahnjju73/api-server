package helmet.bikelab.apiserver.services.myinfo;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserInfo;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserPassword;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.users.UserProfileRequest;
import helmet.bikelab.apiserver.objects.bikelabs.users.UserProfileResponse;
import helmet.bikelab.apiserver.repositories.BikeLabUserInfoRepository;
import helmet.bikelab.apiserver.repositories.BikeLabUserPasswordRepository;
import helmet.bikelab.apiserver.repositories.BikeLabUserRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileService extends SessService {

    private final BikeLabUserPasswordRepository userPasswordRepository;
    private final BikeLabUserRepository userRepository;
    private final BikeLabUserInfoRepository userInfoRepository;

    public BikeSessionRequest fetchMyProfile(BikeSessionRequest request){
        BikeUser session = request.getSessionUser();
        BikeUserInfo bikeUserInfo = session.getBikeUserInfo();
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setDescription(bikeUserInfo.getDescription());
        userProfileResponse.setName(bikeUserInfo.getName());
        userProfileResponse.setEmail(session.getEmail());
        userProfileResponse.setPhone(bikeUserInfo.getPhone());
        request.setResponse(userProfileResponse);
        return request;
    }

    @Transactional
    public BikeSessionRequest modifyProfile(BikeSessionRequest request){
        Map param = request.getParam();
        UserProfileRequest userProfileRequest = map(param, UserProfileRequest.class);
        userProfileRequest.validate();

        BikeUser session = request.getSessionUser();
        BikeUserInfo userInfo = session.getBikeUserInfo();
        if(bePresent(userProfileRequest.getPassword())){
            BikeUserPassword userPassword = session.getBikeUserPassword();
            userPassword.modifyPassword(userProfileRequest.getPassword());
            userPasswordRepository.save(userPassword);
        }
        session.setEmail(userProfileRequest.getEmail());
        userInfo.setDescription(userProfileRequest.getDescription());
        userInfo.setName(userProfileRequest.getName());
        userInfo.setPhone(userProfileRequest.getPhone());
        userRepository.save(session);
        userInfoRepository.save(userInfo);

        return request;
    }
}
