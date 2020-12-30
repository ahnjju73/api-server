package helmet.bikelab.apiserver.config;

import helmet.bikelab.apiserver.objects.exceptions.BusinessException;
import helmet.bikelab.apiserver.objects.exceptions.BusinessExceptionWithMessage;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.*;
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

    @Autowired
    Workspace workspace;

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
            exception.printStackTrace();
            Response response = new Response();
            response.setErrCode("000");
            response.setMessage("HAS ERR");
            response.setResult(HttpStatus.BAD_REQUEST);
            return responseTo(request, response);
        }else {
            Throwable exception = getError(request);
            exception.printStackTrace();
            Response response = new Response();
            response.setErrCode("000");
            response.setMessage("HAS ERR");
            response.setResult(HttpStatus.BAD_REQUEST);
            return responseTo(request, response);
        }
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

        private String message;

        private String errCode;

        private HttpStatus result;

    }
}
