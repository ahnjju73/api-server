package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.domain.bikelab.*;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.objects.SessionResponseDto;
import helmet.bikelab.apiserver.objects.SignUpDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Crypt;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SignService extends SessService {

    @Autowired
    private AutoKey ak;

    @Autowired
    private BikeLabUserRepository userRepository;

    @Autowired
    private BikeLabUserInfoRepository userInfoRepository;

    @Autowired
    private BikeLabUserPasswordRepository passwordRepository;

    @Autowired
    private BikeLabUserSessionRepository sessionRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramUserRepository programUserRepository;

    @Transactional
    public SessionRequest signUp(SessionRequest request){
        Map map = request.getParam();
        SignUpDto signUpDto = map(map, SignUpDto.class);
        userRepository
                .findByEmail(signUpDto.getEmail())
                .ifPresentOrElse(user -> writeError(map, "800-000"), () -> {

                    if(!bePresent(signUpDto.getName())) writeError(map, "800-001");
                    if(!bePresent(signUpDto.getEmail())) writeError(map, "800-002");
                    if(!bePresent(signUpDto.getPhone())) writeError(map, "800-003");

                    Map keyMap = new HashMap();
                    keyMap.put("autono_tp", "admin_user");
                    String userId = ak.makeGetKey(keyMap);

                    BikeLabUser newUser = new BikeLabUser();
                    newUser.setEmail(signUpDto.getEmail());
                    newUser.setUserStatusTypes(BikeUserStatusTypes.COMPLETED);
                    newUser.setUserId(userId);
                    userRepository.save(newUser);

                    BikeLabUserInfo userInfo = new BikeLabUserInfo();
                    userInfoRepository.save(userInfo);

                    BikeLabUserPassword password = new BikeLabUserPassword();
                    password.setBikeUserNo(newUser.getUserNo());
                    String cryptedPassword = Crypt.newCrypt().SHA256(signUpDto.getEmail());
                    String salt = Crypt.newCrypt().getSalt(128);
                    password.setPassword(Crypt.newCrypt().getPassword(cryptedPassword, salt));
                    password.setSalt(salt);
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
    public SessionResponseDto signIn(SessionRequest request){
        Map param = request.getParam();
        String email = (String)param.get("email");
        String password = (String)param.get("password");

        if(!bePresent(email)) writeError(param, "800-004");
        if(!bePresent(password)) writeError(param, "800-005");
        AtomicReference<SessionResponseDto> sessionResponseDto = new AtomicReference<>();
        passwordRepository
                .findByBikeUser_EmailAndBikeUser_UserStatusTypes(email, BikeUserStatusTypes.COMPLETED)
                .ifPresentOrElse(userPassword -> {
                    BikeLabUser user = userPassword.getBikeUser();
                    String requestedPwd = Crypt.newCrypt().getPassword(password, userPassword.getSalt());
                    String crypedPwd = userPassword.getPassword();

                    if(!bePresent(requestedPwd) || !bePresent(crypedPwd) || !requestedPwd.equals(crypedPwd))
                        writeError(param, "800-007", HttpStatus.BAD_REQUEST);

                    String sessionKey = setSessionAuthKey(request, user, user.getUserStatusTypes());
                    sessionResponseDto.set(setResponseData(sessionKey, user, user.getUserInfo()));
                }, () -> writeError(param, "800-006"));

        return sessionResponseDto.get();
    }

    @Transactional
    public SessionRequest signOut(SessionRequest request){
        sessionRepository.deleteByBikeUserNoAndSessionTypes(request.getSessUser().getUserNo(), request.getUserSessionTypes());
        return request;
    }

    public String setSessionAuthKey(SessionRequest request, BikeLabUser user, BikeUserStatusTypes bikeUserStatusTypes) {
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

                        BikeLabUserSession userSession = new BikeLabUserSession();
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

    public SessionResponseDto setResponseData(String sessAuthKey, BikeLabUser user, BikeLabUserInfo userInfo) {
        SessionResponseDto sessionResponseDto = new SessionResponseDto();
        sessionResponseDto.setEmail(user.getEmail());
//        sessionResponseDto.setName(userInfo.getUserName());
//        sessionResponseDto.setThumbnail(thumbnail);
//        sessionResponseDto.setStatus(user.getAccountStatusTypes().getAccountStatus());
        sessionResponseDto.setStatus(sessAuthKey);
        sessionResponseDto.setUserId(user.getUserId());
        return sessionResponseDto;
    }

    public SessionResponseDto helloWorldAdmin(SessionRequest request){
        BikeLabUser user = request.getSessUser();
        BikeLabUserInfo userInfo = user.getUserInfo();
        return setResponseData(request.getSessAuthKey(), user, userInfo);
    }

}
