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
                .andRoute(POST("/bikes"), handler::addBike)
                .andRoute(PUT("/bikes"), handler::updateBike)
                .andRoute(DELETE("/bikes"), handler::deleteBike)
                .andRoute(GET("/bikes/models"), handler::fetchBikeModels);
    }
}
