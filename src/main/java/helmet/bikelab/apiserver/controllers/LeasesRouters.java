package helmet.bikelab.apiserver.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
public class LeasesRouters {
    @Bean
    public RouterFunction<ServerResponse> leasesRouter(LeasesHandler handler){
        return null;
    }
}
