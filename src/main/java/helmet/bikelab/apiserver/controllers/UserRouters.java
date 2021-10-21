package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class UserRouters {

    @Bean
    public RouterFunction<ServerResponse> myProfileRouters(MyProfileHandlers handler){
        return RouterFunctions
                .route(GET("/bike-labs/profiles"), handler::fetchMyProfile)
                .andRoute(PATCH("/bike-labs/profiles"), handler::modifyProfile)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> systemRouters() {
        return RouterFunctions
                .route(GET("/api/checkout"), request -> ServerResponse.ok().body(
                        Mono.fromSupplier(() ->
                                new HashMap()).subscribeOn(Schedulers.boundedElastic()), Map.class)
                );
    }
}
