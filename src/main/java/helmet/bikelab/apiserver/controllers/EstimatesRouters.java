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
public class EstimatesRouters {
    private final EstimatesHandler estimatesHandler;

    @Bean
    public RouterFunction<ServerResponse> estimatesRouter() {
        return RouterFunctions
                .route(GET("/estimates/unpaid-clients"), estimatesHandler::fetchClientsWithUnpaidEstimates)
                .andRoute(GET("/estimates/unpaid"), estimatesHandler::fetchUnpaidEstimates)
                .andRoute(PATCH("/estimates/pay-unpaid/{client_id}"), estimatesHandler::payUnpaidEstimatesByClients)
                .andRoute(GET("/estimate/{estimate_id}"), estimatesHandler::fetchEstimate)
                .andRoute(GET("/estimates"), estimatesHandler::fetchEstimateList)
                .andRoute(GET("/estimates/excel"), estimatesHandler::excelDownloadEstimates)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> customEstimatesRouter() {
        return RouterFunctions
                .route(GET("/custom-estimates"), estimatesHandler::fetchCustomEstimateList)
                .andRoute(GET("/custom-estimates/{custom_estimate_id}"), estimatesHandler::fetchCustomEstimateDetail)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> reviewsRouter() {
        return RouterFunctions
                .route(GET("/reviews"), estimatesHandler::fetchReviewList)
                .andRoute(GET("/reviews/{estimate_id}"), estimatesHandler::fetchEstimate)
                ;
    }
}
