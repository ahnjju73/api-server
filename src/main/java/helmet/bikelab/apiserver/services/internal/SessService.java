package helmet.bikelab.apiserver.services.internal;

import com.google.gson.Gson;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.repositories.BikeLabUserSessionRepository;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

@Service
public abstract class SessService extends Workspace {

    static final Logger logger = LoggerFactory.getLogger(SessService.class);

    @Autowired
    private BikeLabUserSessionRepository bikeLabUserSessionRepository;

    public SessionRequest makeData(ServerRequest request, Map post){
        post.putAll(request.queryParams().toSingleValueMap());
        String lang = (String)post.get("lang");
        String sessType = (String)post.get("session_type");
        post.put("lang", (lang == null || "".equals(lang)) ? "kr" : lang.toLowerCase());
        try{
            post.put(SESSION.SESS_AUTH_KEY, request.headers().header(SESSION.TOKEN_NAME).get(0));
        }catch (Exception e){}
        UserSessionTypes userSessionTypes = UserSessionTypes.getSession(sessType);
        if(userSessionTypes == null){
            userSessionTypes = UserSessionTypes.WEB;
        }
        SessionRequest sessionRequest = SessionRequest.makeSessionRequest(request, post, userSessionTypes);
        apiLogger(sessionRequest);
        return sessionRequest;
    }

    public SessionRequest makeData(ServerRequest request){
        return makeData(request, new HashMap());
    }

    public SessionRequest checkSession(SessionRequest request){
        return checkSession(request, true);
    }
    public SessionRequest checkSession(SessionRequest request, boolean throwError){
        Map param = request.getParam();
        try {
            String sessAuthKey = request.getSessAuthKey();
            Claims body = parseClaimsFromSessionKey(sessAuthKey);
            Integer userId = body.get("user_id", Integer.class);
//            bikeLabUserSessionRepository
//                    .findByUser_UserIdAndSessionType(userId, request.getUserSessionTypes())
//                    .ifPresentOrElse(session -> {
//                        Users user = session.getUser();
//                        String _sessAuthKey = session.getSessionKey();
//
//                        if(!sessAuthKey.equals(_sessAuthKey) && throwError)
//                            // todo: 세션키 오류
//                            writeError(sessionRequest.getParam(), "001-002", HttpStatus.UNAUTHORIZED);
//                        else if(sessAuthKey.equals(_sessAuthKey)){
//                            param.put("sess_user_no", user.getUserId());
//                            request.setSessUser(session.getUser());
//                        }
//                    }, () -> {
//                        if(throwError) writeError(param, "001-002", HttpStatus.UNAUTHORIZED);
//                    });

        }catch (Exception e){
            if(throwError) writeError(param, "001-002", HttpStatus.UNAUTHORIZED);
        }
        return request;
    }

    private Claims parseClaimsFromSessionKey(String sessAuthKey){
        int i = sessAuthKey.lastIndexOf('.');
        String withoutSignature = sessAuthKey.substring(0, i+1);
        Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
        return untrusted.getBody();
    }

    public Map returnData(SessionRequest sessionRequest){
        return sessionRequest.getResponse();
    }

    public void apiLogger(SessionRequest sessionRequest){
        StopWatch stopWatch = sessionRequest.getStopWatch();
        ServerRequest serverRequest = sessionRequest.getServerRequest();
        stopWatch.stop();
        Map logData = new HashMap();
        logData.put("uri", serverRequest.path());
        logData.put("remote_address", serverRequest.remoteAddress().get().getAddress());
        logData.put("path", serverRequest.path());
        logData.put("uri", serverRequest.uri());
        logData.put("query-strings", serverRequest.queryParams().toSingleValueMap());
        logData.put("method", serverRequest.method());
        logData.put("latency_timemillis", stopWatch.getTotalTimeMillis());
        logData.put("date_requested", sessionRequest.getSessNow());
        if(!"/api/checkout".equals(serverRequest.path())){
            logger.info(new Gson().toJson(logData));
        }
    }

    public  <T extends SessionRequest> T getPathVariable(T sessionRequest, String path){
        ServerRequest req = sessionRequest.getServerRequest();
        Map map = sessionRequest.getParam();
        try{
            map.put(path, req.pathVariable(path));
        }catch (Exception e){ map.put(path, ""); }
        sessionRequest.setParam(map);
        return sessionRequest;
    }
}