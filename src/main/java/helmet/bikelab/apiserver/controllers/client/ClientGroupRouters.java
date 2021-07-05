package helmet.bikelab.apiserver.controllers.client;

import helmet.bikelab.apiserver.controllers.BikesHandlers;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@RequiredArgsConstructor
public class ClientGroupRouters {

    private final ClientGroupHandlers clientGroupHandlers;
    private final BikesHandlers bikesHandlers;

    @Bean
    public RouterFunction<ServerResponse> groupRouter(){
        return RouterFunctions
                .route(GET("/client/groups"), clientGroupHandlers::fetchListOfGroup)
                .andRoute(GET("/client/groups/clients"), clientGroupHandlers::fetchClientsByGroup)
                .andRoute(GET("/groups"), bikesHandlers::fetchGroupDetailsByGroupId)
                .andRoute(POST("/client/groups"), clientGroupHandlers::addClientGroup)
                .andRoute(PUT("/client/groups"), clientGroupHandlers::updateClientGroup)
                .andRoute(DELETE("/client/groups"), clientGroupHandlers::deleteClientGroup)
                .andRoute(POST("/client/groups/excel"), clientGroupHandlers::uploadExcel)
                .andRoute(DELETE("/client/groups/force"), clientGroupHandlers::forceDeleteClientGroup);
    }
}
