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
                .andRoute(GET("/leases-extra-payments/group-by-client"), handler::fetchLeaseExtrasGroupByClient)
                .andRoute(GET("/leases-payments-by-index/group-by-client"), handler::fetchLeasePaymentsByClient)
                .andRoute(GET("/leases-payments-by-index"), handler::fetchLeasePaymentsByIndex)
                .andRoute(PUT("/leases-payments-by-index"), handler::readLeaseFeeByPaymentId)
                .andRoute(PUT("/leases-payments-by-index/{payment_id}"), handler::payLeaseFeeByPaymentId)
                .andRoute(PUT("/leases-payments-by-index/extra"), handler::payLeaseExtraFeeByExtraId)
                .andRoute(GET("/leases-payments-by-index/extra"), handler::fetchLeasePaymentExtraByIndex)
                .andRoute(PUT("/unpaid-leases/without-excel/{lease_id}"), handler::payLease)
                .andRoute(PUT("/unpaid-leases/clients/{client_id}"), handler::payClientLease)
                .andRoute(GET("/unpaid-leases/excel/{type}"), handler::unpaidExcelDownload)
                .andRoute(POST("/unpaid-leases/excel/{type}"), handler::payLeaseWithExcel)
                .andRoute(PUT("/unpaid-leases/excel-client"), handler::payLeaseWithClientExcel);
    }

}