package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.bikes.BikeModelService;
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
public class BikesModelHandlers {

    private final BikeModelService bikeModelService;

    public Mono<ServerResponse> doSaveCarModel(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::doSaveCarModel)
                        .map(bikeModelService::returnData), CommonBikes.class);
    }

    public Mono<ServerResponse> updateCarModel(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::updateCarModel)
                        .map(bikeModelService::returnData), CommonBikes.class);
    }

    public Mono<ServerResponse> doSaveManuf(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::doSaveManuf)
                        .map(bikeModelService::returnData), Manufacturers.class);
    }

    public Mono<ServerResponse> updateManuf(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::updateManuf)
                        .map(bikeModelService::returnData), Manufacturers.class);
    }

    public Mono<ServerResponse> fetchModelManufacturer(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::fetchManufacturerCodes)
                        .map(bikeModelService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchCarModelByManufacturer(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::fetchCarModelByManufacturer)
                        .map(bikeModelService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchModelManufacturerByCode(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikeModelService.getPathVariable(req, "code"))
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::fetchModelManufacturerByCode)
                        .map(bikeModelService::returnData), CommonBikes.class);
    }

}
