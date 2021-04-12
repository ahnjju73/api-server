package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class InsurancesRouters {
    @Bean
    public RouterFunction<ServerResponse> insurancesRouter(InsurancesHandler handler){
        return RouterFunctions
                .route(GET("/insurances"), handler::fetchInsurance)
                .andRoute(POST("/insurances"), handler::addInsurance)
                .andRoute(PUT("/insurances"), handler::updateInsurance)
                .andRoute(DELETE("/insurances"), handler::deleteInsurance)
                .andRoute(GET("/insurances/cover-options"), handler::fetchInsuranceOption);
    }
}
