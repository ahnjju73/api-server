package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Component
public class LeasePaymentRouters {
    @Bean
    public RouterFunction<ServerResponse> paymentRouters(LeasePaymentHandlers handler){
        return RouterFunctions
                .route(GET("/unpaid-leases"), handler::fetchLeases)
                .andRoute(PUT("/unpaid-leases/without-excel/{lease_id}"), handler::payLease)
                .andRoute(PUT("/unpaid-leases/clients/{client_id}"), handler::payClientLease)
                .andRoute(GET("/unpaid-leases/excel"), handler::unpaidExcelDownload)
                .andRoute(POST("/unpaid-leases/excel"), handler::payLeaseWithExcel);
    }
}
