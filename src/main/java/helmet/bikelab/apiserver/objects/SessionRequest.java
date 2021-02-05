package helmet.bikelab.apiserver.objects;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUser;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StopWatch;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Session request.
 */
@Setter
@Getter
public class SessionRequest {

    private BikeLabUser sessUser;

    private UserSessionTypes userSessionTypes;

    private String sessAuthKey;

    private LocalDateTime sessNow = LocalDateTime.now();

    private Map param = new HashMap();

    private Map response = new HashMap();

    private StopWatch stopWatch = new StopWatch();

    private ServerRequest serverRequest;

    public static <T extends SessionRequest> SessionRequest makeSessionRequest(ServerRequest request, Map post, UserSessionTypes userSessionTypes){
        SessionRequest sessionRequest = new SessionRequest();
        sessionRequest.getStopWatch().start();
        sessionRequest.setSessAuthKey((String)post.get(SESSION.SESS_AUTH_KEY));
        sessionRequest.setParam(post);
        sessionRequest.setServerRequest(request);
        sessionRequest.setUserSessionTypes(userSessionTypes);
        return sessionRequest;
    }

}
