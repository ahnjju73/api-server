package helmet.bikelab.apiserver.config;

import com.google.gson.Gson;
import helmet.bikelab.apiserver.objects.exceptions.BusinessException;
import helmet.bikelab.apiserver.objects.exceptions.BusinessExceptionWithMessage;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class ErrorGlobalHandler<T extends BusinessException> extends AbstractErrorWebExceptionHandler {

    static final Logger logger = LoggerFactory.getLogger(SessService.class);

    @Autowired
    private Workspace workspace;

    public ErrorGlobalHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);
        if(error instanceof BusinessException){
            BusinessException businessException = (BusinessException) getError(request);
            businessException.printStackTrace();
            Response response = new Response();
            response.setErrCode(businessException.getErr_code());
            response.setMessage(businessException.getMsg());
            response.setResult(businessException.getErrHttpStatus());
            return responseTo(request, response);
        }else if(error instanceof BusinessExceptionWithMessage){
            BusinessExceptionWithMessage businessExceptionWithMessage = (BusinessExceptionWithMessage) getError(request);
            String messageCode = businessExceptionWithMessage.getMessageCode();
            HttpStatus errHttpStatus = businessExceptionWithMessage.getErrHttpStatus() == null ? HttpStatus.BAD_REQUEST : businessExceptionWithMessage.getErrHttpStatus();
            Map map = new HashMap();
            map.put("lang_code", messageCode == null ? "" : messageCode);
            map.put("lang", "kr");
            String errMessage = (String)workspace.getItem("comm.common.getMessage", map);
            Response response = new Response();
            response.setErrCode(messageCode);
            response.setMessage(errMessage == null ? "" : errMessage);
            response.setResult(errHttpStatus);
            return responseTo(request, response);
        }else if(error instanceof Exception){
            Exception exception = (Exception) getError(request);
            apiLogger(HttpStatus.BAD_REQUEST, exception);
            return responseTo(request, new Response());
        }else {
            Throwable exception = getError(request);
            apiLogger(HttpStatus.BAD_REQUEST, exception);
            return responseTo(request, new Response());
        }
    }

    private void apiLogger(HttpStatus httpStatus, Throwable exception){
        Map logData = new HashMap();
        logData.put("status", httpStatus);
        logData.put("error_message", exception.getMessage());
        logger.error(new Gson().toJson(logData));
    }

    private Mono<ServerResponse> responseTo(ServerRequest request, Response response){
        return ServerResponse.status(response.getResult())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(response), Response.class);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    class Response {

        private String message = "HAS ERR";

        private String errCode = "000";

        private HttpStatus result = HttpStatus.BAD_REQUEST;

    }
}
