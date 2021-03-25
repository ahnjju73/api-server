package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.fines.FinesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class FinesHandlers {
    private final FinesService finesService;

    public Mono<ServerResponse> fetchFine(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> finesService.makeSessionRequest(request, BikeSessionRequest.class))
                    .subscribeOn(Schedulers.elastic())
                    .map(req -> finesService.getPathVariable(req, "fine_num"))
                    .map(finesService::checkBikeSession)
                    .map(finesService::fetchFine)
                    .map(finesService::returnData), Map.class);
    }

    public Mono<ServerResponse> addFine(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> finesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(finesService::checkBikeSession)
                        .map(finesService::addFine)
                        .map(finesService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateFine(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> finesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(finesService::checkBikeSession)
                        .map(finesService::updateFine)
                        .map(finesService::returnData), Map.class);
    }
}
