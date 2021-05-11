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
                .andRoute(POST("/lease-extra"), handler::addExtra)
                .andRoute(GET("/lease-extra/{payment_id}"), handler::fetchExtra)
                .andRoute(PUT("/lease-extra/{extra_id}"), handler::updateExtra)
                .andRoute(DELETE(".lease-extra"), handler::deleteExtra);

//                .andRoute(PUT("/lease-extra"), handler::updateExtra)
//                .andRoute(DELETE("/lease-extra"), handler::deleteExtra);
    }
}
