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
    public RouterFunction<ServerResponse> bikeInsurancesRouter(BikeInsurancesHandler handler){
        return RouterFunctions
                .route(GET("/insurances/bikes"), handler::getBikeInsurances)
                .andRoute(GET("/insurances/bikes/details"), handler::getBikeInsuranceDetails)
                .andRoute(POST("/insurances/bikes"), handler::addNewBikeInsurance)
                .andRoute(PUT("/insurances/bikes"), handler::updateBikeInsurance)
                .andRoute(DELETE("/insurances/bikes"), handler::deleteBikeInsurance)
                .andRoute(POST("/insurances/bikes/renew"), handler::renewBikeInsurance)

                .andRoute(POST("/insurances/bikes/setter"), handler::setBikeInsuranceToBike)
                .andRoute(DELETE("/insurances/bikes/setter"), handler::removeBikeInsuranceToBike)
                .andRoute(POST("/insurances/bikes/enable"), handler::checkIfEnableBikeInsuranceToBike)
                .andRoute(PUT("/insurances/bikes/pay"), handler::payInsuranceFeeByNo)
                ;
    }


    @Bean
    public RouterFunction<ServerResponse> insurancesRouter(InsurancesHandler handler){
        return RouterFunctions
                .route(GET("/insurances"), handler::fetchInsurance)
                .andRoute(POST("/insurances"), handler::addInsurance)
                .andRoute(PUT("/insurances"), handler::updateInsurance)
                .andRoute(DELETE("/insurances"), handler::deleteInsurance)
                .andRoute(GET("/insurances/cover-options"), handler::fetchInsuranceOption);
    }

    @Bean
    public RouterFunction<ServerResponse> RiderInsurancesRouter(InsurancesHandler handler){
        return RouterFunctions
                .route(GET("/rider-insurances"), handler::fetchRiderInsurances)
                .andRoute(GET("/rider-insurances/{rider_ins_id}"), handler::fetchRiderInsuranceDetail)
                .andRoute(POST("/rider-insurances"), handler::addRiderInsurance)
                .andRoute(PUT("/rider-insurances/{rider_ins_id}"), handler::updateRiderInsurance)
                .andRoute(DELETE("/rider-insurances/{rider_ins_id}"), handler::deleteRiderInsurance)
                .andRoute(PATCH("/rider-insurances/{rider_ins_id}/renew"), handler::renewRiderInsurance)
                .andRoute(PATCH("/rider-insurances/{rider_ins_id}/confirm"), handler::confirmInsurance)
                .andRoute(PATCH("/rider-insurances/{rider_ins_id}/stop"), handler::stopInsurance)
                .andRoute(PUT("/rider-insurances-detail"), handler::updateRiderInsuranceDetail)
                .andRoute(POST("/rider-insurances-pre-signed-url"),handler::generatePresignedUrl)
                .andRoute(POST("/rider-insurances-message"),handler::sendSMSMessage)
                ;

    }
}
