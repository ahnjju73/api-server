package helmet.bikelab.apiserver.controllers.client;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;

@Component
public class ClientsRouters {
    @Bean
    public RouterFunction<ServerResponse> clientRouter(ClientsHandlers handler){
        return RouterFunctions
                .route(GET("/clients"), handler::fetchListOfClients)
                .andRoute(GET("/clients/{client_id}"), handler::fetchClientDetailByClientId)
                .andRoute(POST("/clients"), handler::addClient)
                .andRoute(PUT("/clients"), handler::updateClient)
                .andRoute(DELETE("/clients"), handler::deleteClient)
                .andRoute(PUT("/clients/password"), handler::resetPassword)
                .andRoute(GET("/clients/{client_id}/histories"), handler::fetchHistoryOfClient);
    }
    @Bean
    public RouterFunction<ServerResponse> clientOverpayRouter(ClientsHandlers handler){
        return RouterFunctions
                .route(GET("/clients/overpay/{client_id}"), handler::fetchClientOverpay);
    }
}
