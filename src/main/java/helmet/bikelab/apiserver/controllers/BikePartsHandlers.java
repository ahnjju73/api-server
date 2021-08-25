package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.bikes.BikePartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BikePartsHandlers {

    private final BikePartsService partsService;

    public Mono<ServerResponse> fetchParts(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchParts)
                        .map(partsService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchPartsCodes(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchPartsCodes)
                        .map(partsService::returnData), Parts.class);
    }

    public Mono<ServerResponse> addPartsByModel(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::addPartsByModel)
                        .map(partsService::returnData), Map.class);
    }
}
