package helmet.bikelab.apiserver.controllers.endusers;

import helmet.bikelab.apiserver.controllers.client.ClientsHandlers;
import helmet.bikelab.apiserver.repositories.ShopsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Component
@RequiredArgsConstructor
public class ShopsRouters {

    private final ShopHandlers shopHandlers;

    @Bean
    public RouterFunction<ServerResponse> shopRouter(){
        return RouterFunctions
                .route(POST("/shops"), shopHandlers::registerNewShop)
                ;
    }
}
