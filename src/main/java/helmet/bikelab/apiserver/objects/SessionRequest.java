package helmet.bikelab.apiserver.objects;

import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public abstract class SessionRequest<T> extends OriginObject {

    private static final Logger logger = LoggerFactory.getLogger(SessService.class);

    private UserSessionTypes userSessionTypes;

    private String sessAuthKey;

    private LocalDateTime sessNow = LocalDateTime.now();

    private Map param = new HashMap();

    private T response;

    private StopWatch stopWatch = new StopWatch();

    private ServerRequest serverRequest;

    public static <T extends SessionRequest> SessionRequest makeSessionRequest(ServerRequest request, Map post, UserSessionTypes userSessionTypes, Class<T> classes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        SessionRequest sessionRequest = classes.getConstructor().newInstance();
        sessionRequest.getStopWatch().start();
        sessionRequest.setSessAuthKey((String)post.get(SESSION.SESS_AUTH_KEY));
        sessionRequest.setParam(post);
        sessionRequest.setServerRequest(request);
        sessionRequest.setUserSessionTypes(userSessionTypes);
        return sessionRequest;
    }

}