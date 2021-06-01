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
                .andRoute(GET("/leases/{lease_id}"), handler::fetchLease)
                .andRoute(POST("/leases"), handler::addLease)
                .andRoute(PUT("/leases/{lease_id}"), handler::updateLease)
                .andRoute(PUT("/leases/change_status/submit"), handler::submitApplication)
                .andRoute(PUT("/leases/change_status/confirm"), handler::confirmApplication)
                .andRoute(PUT("/leases/change_status/reject"), handler::rejectLease)
                .andRoute(DELETE("/leases"), handler::deleteLease);
    }
}
