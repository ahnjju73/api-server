package helmet.bikelab.apiserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@RequiredArgsConstructor
public class InsuranceCompanyRouters {
    @Bean
    public RouterFunction<ServerResponse> insuranceCompanyRouter(InsuranceCompanyHandler handler) {
        return RouterFunctions
                .route(GET("/insurance-companies"), handler::fetchInsuranceCompanies)
                .andRoute(GET("/insurance-companies/{company_id}"), handler::fetchInsCompanyDetail)
                .andRoute(POST("/insurance-companies"), handler::addInsuranceCompany)
                .andRoute(PUT("/insurance-companies/{company_id}"), handler::updateInsCompany)
                .andRoute(PATCH("/insurance-companies/{company_id}"), handler::resetPassword)
                ;
    }
}
