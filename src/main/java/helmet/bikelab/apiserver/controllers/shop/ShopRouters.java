package helmet.bikelab.apiserver.controllers.shop;

import helmet.bikelab.apiserver.controllers.DemandLeaseHandler;
import helmet.bikelab.apiserver.controllers.LeaseExpenseHandler;
import helmet.bikelab.apiserver.controllers.LeasesHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@RequiredArgsConstructor
public class ShopRouters {

    private final ShopHandler shopHandler;

    @Bean
    public RouterFunction<ServerResponse> shopRouter(){
        return RouterFunctions
                .route(POST("/repairshop"), shopHandler::registerNewShop)
                .andRoute(PUT("/repairshop"), shopHandler::updateShopInfo)
                .andRoute(GET("/repairshop/details"), shopHandler::fetchShopDetailsByShopId)
                .andRoute(GET("/repairshop"), shopHandler::fetchAllShops)
                .andRoute(PUT("/repairshop/password"), shopHandler::updatePasswordByShopId)
                .andRoute(GET("/repairshop/histories"), shopHandler::fetchHistoryOfShop)
                ;
    }

}