package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class LeaseExtrasRouters {
    @Bean
    public RouterFunction<ServerResponse> extraRouters(LeaseExtrasHandlers handler){
        return RouterFunctions
                .route(GET("/lease-extra"), handler::fetchExtras)
                .andRoute(GET("/exceed-lease-extra"), handler::fetchExceedExtras)
                .andRoute(POST("/lease-extra"), handler::addExtra)
                .andRoute(GET("/lease-extra/{payment_id}/payment"), handler::fetchExtra)
                .andRoute(GET("/lease-extra/{extra_id}/detail"), handler::fetchDetail)
                .andRoute(PUT("/lease-extra/{extra_id}"), handler::updateExtra)
                .andRoute(DELETE("/lease-extra/{extra_id}"), handler::deleteExtra);

//                .andRoute(PUT("/lease-extra"), handler::updateExtra)
//                .andRoute(DELETE("/lease-extra"), handler::deleteExtra);
    }

    @Bean
    public RouterFunction<ServerResponse> findRouters(LeaseFinesHandlers handler){
        return RouterFunctions
                .route(GET("/lease-fines"), handler::fetchFines)
                .andRoute(GET("/lease-fines/{fine_id}"), handler::addFine)
                .andRoute(POST("/lease-fines"), handler::addFine)
                .andRoute(PUT("/lease-fines"), handler::updateFine)
                .andRoute(DELETE("/lease-fines"), handler::deleteFine)
                .andRoute(POST("/lease-fines/generate-presigned"), handler::generatePresignedUrl)
                .andRoute(GET("/lease-fines/attachments"), handler::fetchAttachments)
                .andRoute(POST("/lease-fines/attachments"), handler::addFineAttachment)
                .andRoute(DELETE("/lease-fines/attachments"), handler::deleteFineAttachment)
                ;
    }
}
