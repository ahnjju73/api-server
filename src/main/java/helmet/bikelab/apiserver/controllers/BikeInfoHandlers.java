package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.bike.BikeInfo;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.bikes.BikeInfoService;
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
public class BikeInfoHandlers {

    private final BikeInfoService bikeInfoService;

    public Mono<ServerResponse> getBikeInfoListByBikeId(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeInfoService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeInfoService::checkBikeSession)
                        .map(bikeInfoService::getBikeInfoListByBikeId)
                        .map(bikeInfoService::returnData), List.class);
    }

    public Mono<ServerResponse> deleteBikeInfo(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeInfoService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeInfoService::checkBikeSession)
                        .map(bikeInfoService::deleteBikeInfo)
                        .map(bikeInfoService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateBikeInfo(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeInfoService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeInfoService::checkBikeSession)
                        .map(bikeInfoService::updateBikeInfo)
                        .map(bikeInfoService::returnData), BikeInfo.class);
    }

    public Mono<ServerResponse> addBikeInfo(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeInfoService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeInfoService::checkBikeSession)
                        .map(bikeInfoService::addBikeInfo)
                        .map(bikeInfoService::returnData), BikeInfo.class);
    }

}
