package helmet.bikelab.apiserver.controllers.client;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class ClientGroupRouters {

    @Bean
    public RouterFunction<ServerResponse> groupRouter(ClientGroupHandlers handler){
        return RouterFunctions
                .route(GET("/client/groups"), handler::uploadExcel)
                .andRoute(GET("/client/groups/clients"), handler::fetchClientsByGroup)
                .andRoute(POST("/client/groups"), handler::addClientGroup)
                .andRoute(PUT("/client/groups"), handler::updateClientGroup)
                .andRoute(DELETE("/client/groups"), handler::deleteClientGroup)
                .andRoute(POST("/client/groups/excel"), handler::uploadExcel)
                .andRoute(DELETE("/client/groups/force"), handler::forceDeleteClientGroup);
    }
}
