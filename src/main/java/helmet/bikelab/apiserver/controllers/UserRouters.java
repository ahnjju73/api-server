package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

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

}
