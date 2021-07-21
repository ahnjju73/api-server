package helmet.bikelab.apiserver.controllers.endusers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.clients.ClientsService;
import helmet.bikelab.apiserver.services.shops.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ShopHandlers {
    private final ShopService shopService;

    public Mono<ServerResponse> registerNewShop(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> shopService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(shopService::checkBikeSession)
                        .map(shopService::registerNewShop)
                        .map(shopService::returnData), Map.class);
    }

}
