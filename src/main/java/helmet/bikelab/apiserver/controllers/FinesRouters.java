package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class FinesRouters {
    @Bean
    public RouterFunction<ServerResponse> finesRouter(FinesHandlers handler){
        return RouterFunctions
                .route(GET("/fines/{fine_id}"), handler::fetchFine)
                .andRoute(GET("/fines"), handler::fetchFines)
                .andRoute(POST("/fines"), handler::addFine)
                .andRoute(DELETE("/fines"), handler::deleteFine)
                .andRoute(PUT("/fines"), handler::updateFine);
    }
}
