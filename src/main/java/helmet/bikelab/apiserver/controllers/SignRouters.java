package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class SignRouters {

    @Bean
    public RouterFunction<ServerResponse> adminSignRouters(SignHandlers handler){
        return RouterFunctions
                .route(POST("/new-users"), handler::signUpUser)
                .andRoute(POST("/login"), handler::signInUser)
                .andRoute(GET("/hello-world"), handler::helloWorld)
                .andRoute(DELETE("/see-you"), handler::signOut)
                ;
    }
}
