package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.bikes.BikePartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BikePartsHandlers {
    private final BikePartsService partsService;

    public Mono<ServerResponse> fetchParts(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchParts)
                        .map(partsService::returnData), java.util.Map.class);
    }
}
