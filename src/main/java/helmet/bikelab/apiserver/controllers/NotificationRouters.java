package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class NotificationRouters {

    @Bean
    public RouterFunction<ServerResponse> notiRouters(NotificationHandlers handler) {
        return RouterFunctions
                .route(GET("/notifications"), handler::fetchNotifications)
                .andRoute(GET("/notifications/{notification_no}"), handler::fetchNotificationDetail)
                .andRoute(POST("/notifications"), handler::makeNotification)
                .andRoute(POST("/notifications-presigned"), handler::generatePresignedUrl)
                .andRoute(PUT("/notifications"), handler::updateNotification)
                .andRoute(DELETE("/notifications"), handler::deleteNotification)
                ;
    }
}
