package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class LeasesRouters {
    @Bean
    public RouterFunction<ServerResponse> leasesRouter(LeasesHandler handler){
        return RouterFunctions
                .route(GET("/leases"), handler::fetchLeases)
                .andRoute(POST("/leases"), handler::addLease)
                .andRoute(PUT("/leases/{lease_id}"), handler::updateLease);
//                .andRoute(DELETE("/leases"), handler::deleteInsurance);
    }
}
