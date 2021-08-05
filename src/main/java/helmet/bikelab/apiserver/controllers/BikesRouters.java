package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.controllers.client.ClientsHandlers;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;

@Component
public class BikesRouters {
    @Bean
    public RouterFunction<ServerResponse> bikeRouter(BikesHandlers handler){
        return RouterFunctions
                .route(GET("/bikes"), handler::fetchListOfBikes)
                .andRoute(GET("/bikes/{bike_id}"), handler::fetchBikeDetail)
                .andRoute(GET("/bikes/client/{client_id}"),handler::fetchBikesByClient)
                .andRoute(POST("/bikes"), handler::addBike)
                .andRoute(PUT("/bikes"), handler::updateBike)
                .andRoute(DELETE("/bikes"), handler::deleteBike)
                .andRoute(GET("/bikes_model"), handler::fetchBikeModels)
                .andRoute(POST("/bikes_model"), handler::addBikeModel)
                .andRoute(PUT("/bikes_model"), handler::updateBikeModel)
                .andRoute(GET("/bikes_no_lease"), handler::fetchBikesWithoutLease)
                .andRoute(GET("/bikes/{bike_id}/histories"), handler::fetchHistoriesByBikeId)
                ;
    }

    @Bean
    public RouterFunction<ServerResponse> bikeFileRouter(BikesHandlers handler){
        return RouterFunctions
                .route(POST("/bike_file/{bike_id}/generate_pre_sign"), handler::generatePreSign)
                .andRoute(POST("/bike_file/{bike_id}/check_upload"), handler::checkUpload)
                .andRoute(GET("/bike-file/{bike_id}"), handler::fetchBikeFiles)
                .andRoute(DELETE("/bike-file/{bike_attachment_no}"), handler::deleteBikeFile);
    }
}
