package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class SignRouters {

    @Bean
    public RouterFunction<ServerResponse> bikeSignRouters(SignHandlers handler){
        return RouterFunctions
                .route(POST("/bike-labs/new-users"), handler::addBikeUser)
                .andRoute(POST("/bike-labs/login"), handler::signInUser)
                .andRoute(GET("/bike-labs/hello-world"), handler::helloWorld)
                .andRoute(DELETE("/bike-labs/see-you"), handler::signOut)
                ;
    }
}
