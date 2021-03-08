package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.domain.bikelab.*;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.objects.SessionResponseDto;
import helmet.bikelab.apiserver.objects.bikelabs.NewBikeUserDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class SignService extends SessService {

    private final AutoKey ak;

    private final BikeLabUserRepository userRepository;
    private final BikeLabUserInfoRepository userInfoRepository;
    private final BikeLabUserPasswordRepository passwordRepository;
    private final BikeLabUserSessionRepository sessionRepository;
    private final ProgramRepository programRepository;
    private final ProgramUserRepository programUserRepository;

    @Transactional
    public SessionRequest addBikeUser(SessionRequest request){
        Map map = request.getParam();
        NewBikeUserDto newBikeUserDto = map(map, NewBikeUserDto.class);
        userRepository
                .findByEmail(newBikeUserDto.getEmail())
                .ifPresentOrElse(user -> withException("800-000"), () -> {

                    if(!bePresent(newBikeUserDto.getName())) writeError(map, "800-001");
                    if(!bePresent(newBikeUserDto.getEmail())) writeError(map, "800-002");
//                    if(!bePresent(signUpDto.getPhone())) writeError(map, "800-003");

                    Map keyMap = new HashMap();
                    keyMap.put("autono_tp", "bike_user");
                    String userId = ak.makeGetKey(keyMap);

                    BikeUser newUser = new BikeUser();
                    newUser.setEmail(newBikeUserDto.getEmail());
                    newUser.setUserStatusTypes(BikeUserStatusTypes.COMPLETED);
                    newUser.setUserId(userId);
                    userRepository.save(newUser);

                    BikeUserInfo userInfo = new BikeUserInfo();
                    userInfo.setBikeUser(newUser);
                    userInfo.setBikeUserNo(newUser.getUserNo());
                    userInfoRepository.save(userInfo);

                    BikeUserPassword password = new BikeUserPassword();
                    password.newPassword(newUser);
                    passwordRepository.save(password);

                    programRepository
                            .findByDefaultType(YesNoTypes.YES)
                            .forEach(program -> {
                                ProgramUser programUser = new ProgramUser();
                                programUser.setProgramNo(program.getProgramNo());
                                programUser.setReadWriting(ReadWriteTypes.ONLY_VIEW);
                                programUser.setBikeUserNo(newUser.getUserNo());
                                programUserRepository.save(programUser);
                            });
                });

        return request;
    }

    @Transactional
    public BikeSessionRequest signIn(BikeSessionRequest request){
        Map param = request.getParam();
        String email = (String)param.get("email");
        String password = (String)param.get("password");

        if(!bePresent(email)) writeError(param, "800-004");
        if(!bePresent(password)) writeError(param, "800-005");
        AtomicReference<SessionResponseDto> sessionResponseDto = new AtomicReference<>();
        passwordRepository
                .findByBikeUser_EmailAndBikeUser_UserStatusTypes(email, BikeUserStatusTypes.COMPLETED)
                .ifPresentOrElse(userPassword -> {
                    BikeUser user = userPassword.getBikeUser();
                    ModelPassword mp = userPassword.getModelPassword();
                    String requestedPwd = Crypt.newCrypt().getPassword(password, mp.getSalt());
                    String crypedPwd = mp.getPassword();

                    if(!bePresent(requestedPwd) || !bePresent(crypedPwd) || !requestedPwd.equals(crypedPwd))
                        writeError(param, "800-007", HttpStatus.BAD_REQUEST);

                    String sessionKey = setSessionAuthKey(request, user, user.getUserStatusTypes());
                    sessionResponseDto.set(setResponseData(sessionKey, user, user.getBikeUserInfo()));
                }, () -> writeError(param, "800-006"));

        request.setResponse(sessionResponseDto.get());
        return request;
    }

    @Transactional
    public BikeSessionRequest signOut(BikeSessionRequest request){
        sessionRepository.deleteByBikeUserNoAndSessionTypes(request.getSessionUser().getUserNo(), request.getUserSessionTypes());
        return request;
    }

    public String setSessionAuthKey(BikeSessionRequest request, BikeUser user, BikeUserStatusTypes bikeUserStatusTypes) {
        Map param = request.getParam();
        UserSessionTypes userSessionTypes = request.getUserSessionTypes();
        AtomicReference<String> sessionKey = new AtomicReference<>("");
        userInfoRepository
                .findByBikeUserNo(user.getUserNo())
                .ifPresentOrElse(userInfo -> {
                    try {
                        String salt = Crypt.newCrypt().getSalt(8);
                        sessionKey.set(Jwts.builder()
                                .setIssuer(SESSION.TOKEN_ISSURE)
                                .setSubject(SESSION.TOKEN_NAME)
                                .claim("user_no", user.getUserNo())
                                .claim("sess_now", request.getSessNow())
                                .setIssuedAt(new Date())
                                .signWith(
                                        SignatureAlgorithm.HS256,
                                        salt.getBytes("UTF-8")
                                ).compact());

                        // Session Key
                        request.setSessAuthKey(sessionKey.get());

                        sessionRepository.deleteByBikeUserNoAndSessionTypes(user.getUserNo(), userSessionTypes);

                        BikeUserSession userSession = new BikeUserSession();
                        userSession.setBikeUserNo(user.getUserNo());
                        userSession.setUser(user);
                        userSession.setSalt(salt);
                        userSession.setSessionKey(sessionKey.get());
                        userSession.setSessionTypes(userSessionTypes);

                        sessionRepository.save(userSession);
                    } catch (Throwable e) {
                        writeError(param, "800-007");
                    }
                }, () -> writeError(param, "001-001"));
        return sessionKey.get();
    }

    public SessionResponseDto setResponseData(String sessAuthKey, BikeUser user, BikeUserInfo userInfo) {
        BikeUserStatusTypes userStatusTypes = user.getUserStatusTypes();
        SessionResponseDto sessionResponseDto = new SessionResponseDto();
        sessionResponseDto.setEmail(user.getEmail());
        sessionResponseDto.setSessionKey(sessAuthKey);
        sessionResponseDto.setStatus(userStatusTypes);
        sessionResponseDto.setStatusCode(userStatusTypes.getStatus());
        sessionResponseDto.setUserId(user.getUserId());
        return sessionResponseDto;
    }

    public BikeSessionRequest helloWorldAdmin(BikeSessionRequest request){
        BikeUser user = request.getSessionUser();
        BikeUserInfo userInfo = user.getBikeUserInfo();
        SessionResponseDto sessionResponseDto = setResponseData(request.getSessAuthKey(), user, userInfo);
        request.setResponse(sessionResponseDto);
        return request;
    }

}
