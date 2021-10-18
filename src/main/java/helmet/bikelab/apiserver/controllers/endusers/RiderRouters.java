package helmet.bikelab.apiserver.controllers.endusers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@RequiredArgsConstructor
public class RiderRouters {

    private final RiderHandlers riderHandlers;

    @Bean
    public RouterFunction<ServerResponse> riderCommonRouter(){
        return RouterFunctions
                .route(GET("/riders/requested"), riderHandlers::fetchRiderBikeRequests)
                .andRoute(POST("/riders/assign"), riderHandlers::assignRiderToBike)
                .andRoute(PUT("/riders/requested/approve"), riderHandlers::doApproveRider)
                .andRoute(PUT("/riders/requested/decline"), riderHandlers::doDeclineRider)
                .andRoute(GET("/riders/by-bike"), riderHandlers::fetchRiderListByBike)
                .andRoute(GET("/riders"), riderHandlers::fetchRiders)
                .andRoute(GET("/riders-by/verified"), riderHandlers::fetchRidersVerified)
                .andRoute(GET("/riders-by/lease-requested"), riderHandlers::fetchRidersLeaseRequested)
                .andRoute(POST("/riders"), riderHandlers::addNewRiders)
                .andRoute(GET("/riders-verified"), riderHandlers::fetchRiderVerified)
                .andRoute(GET("/riders/{rider_id}"), riderHandlers::fetchRiderDetail)
                .andRoute(PUT("/riders/{rider_id}/update"), riderHandlers::updateRider)
                .andRoute(DELETE("/riders/{rider_id}"), riderHandlers::stopRider)
                .andRoute(PUT("/riders/reset-password"), riderHandlers::resetPassword)
                .andRoute(GET("/riders/bike-history/{rider_id}"), riderHandlers::fetchRiderBikeHistories)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> riderVerified(){
        return RouterFunctions
                .route(PUT("/riders-verified/approve"), riderHandlers::doApproveRiderVerified)
                .andRoute(PUT("/riders-verified/reject"), riderHandlers::doRejectRiderVerified)
                ;
    }
}
