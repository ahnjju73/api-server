package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
public class SystemRouters {

    @Bean
    public RouterFunction<ServerResponse> menuRouters(SystemHandlers handler){
        return RouterFunctions
                .route(GET("/bike-labs/systems/menu"), handler::fetchMyLeftMenu)
                .andRoute(GET("/bike-labs/systems/menus"), handler::fetchAllMenus)
                .andRoute(GET("/bike-labs/systems/menu/users"), handler::fetchUsersMenu)
                .andRoute(PATCH("/bike-labs/systems/menu/users/authorizations"), handler::handlePermissionToUser)
                .andRoute(PATCH("/bike-labs/systems/menu/users/permission"), handler::changeUserPermissionReadWrite)
                .andRoute(GET("/bike-labs/authorizations/mine"), handler::checkAuthorization)
                .andRoute(GET("/check-loadbalancer"), handler::checkLoadbalancer)
                ;
    }
}
