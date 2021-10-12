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
                ;
    }
}
