package helmet.bikelab.apiserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import helmet.bikelab.apiserver.objects.exceptions.BusinessException;
import helmet.bikelab.apiserver.objects.exceptions.BusinessExceptionWithMessage;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class ErrorGlobalHandler<T extends BusinessException> extends AbstractErrorWebExceptionHandler {

    static final Logger logger = LoggerFactory.getLogger(SessService.class);

    @Autowired
    private Workspace workspace;

    @Value("${system.is-release}")
    private Boolean isRelease;

    @Value("${application.monitoring}")
    private Boolean isMonitoring;

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
            if(!isRelease) businessException.printStackTrace();
            Response response = new Response();
            response.setErrCode(businessException.getErr_code());
            response.setMessage(businessException.getMsg());
            response.setResult(businessException.getErrHttpStatus());
            return responseTo(request, response);
        }else if(error instanceof BusinessExceptionWithMessage){
            BusinessExceptionWithMessage businessExceptionWithMessage = (BusinessExceptionWithMessage) getError(request);
            if(!isRelease) businessExceptionWithMessage.printStackTrace();
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
            errorToSlack(exception, request);
            return responseTo(request, new Response());
        }else {
            Throwable exception = getError(request);
            apiLogger(HttpStatus.BAD_REQUEST, exception);
            errorToSlack(exception, request);
            return responseTo(request, new Response());
        }
    }

    private void apiLogger(HttpStatus httpStatus, Throwable exception){
        exception.printStackTrace();
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

    private <K extends Throwable> void errorToSlack(K exception, ServerRequest serverRequest){
        if(exception instanceof ResponseStatusException){
            ResponseStatusException responseStatusException = (ResponseStatusException) exception;
            if(responseStatusException.getStatus().equals(HttpStatus.NOT_FOUND)) return;
        }
        StackTraceElement[] stackTrace = exception.getStackTrace();

        String className = Arrays.stream(stackTrace).map(elm -> elm.getFileName() + "(" + elm.getMethodName() + "::" + elm.getLineNumber() + ")").collect(Collectors.joining(" >\n"));

        Map<String, Object> params = new HashMap<>();
        List blocks = new ArrayList<>();
        Throwable[] suppressed = exception.getSuppressed();
        blocks.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "[SERVICE-API] *<https://backoffice.onus-biz.com/exception?q=" + exception + "|" + exception.getClass().getSimpleName() + ">*\n" + (suppressed != null && suppressed.length > 0 ? suppressed[0].getMessage() : exception)
                )
        ));
        blocks.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "* URI: *\n" + serverRequest.method().name() + " " + serverRequest.uri()
                )
        ));
        List blockSecond = new ArrayList();
        if(exception != null && exception.getMessage() != null && !"".equals(exception.getMessage()) ){
            blockSecond.add(ImmutableMap.of(
                    "type", "section",
                    "text", ImmutableMap.of(
                            "type", "mrkdwn",
                            "text", "* Detail Message:*\n" + exception.getMessage()
                    )
            ));
        }
        blockSecond.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "* ClassName(Method):*\n" + className
                )
        ));
        blockSecond.add(ImmutableMap.of(
                "type", "section",
                "text", ImmutableMap.of(
                        "type", "mrkdwn",
                        "text", "* Update:*\n" + LocalDateTime.now()
                )
        ));
        blockSecond.add(ImmutableMap.of(
                "type", "divider"
        ));
        params.put("blocks", blocks);
        params.put("attachments", Arrays.asList(ImmutableMap.of("blocks", blockSecond)));
        String body = "";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();
            body = mapper.writeValueAsString(params);
            if(body != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
                HttpEntity entity = new HttpEntity(body, headers);
                restTemplate.postForEntity(isRelease ? "https://hooks.slack.com/services/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" : "https://hooks.slack.com/services/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", entity, String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
