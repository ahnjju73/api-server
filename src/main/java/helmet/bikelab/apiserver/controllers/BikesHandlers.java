package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.bikes.BikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class BikesHandlers {
    private final BikesService bikesService;

    public Mono<ServerResponse> fetchListOfBikes(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikes)
                        .map(bikesService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchBikeDetail(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikesService.getPathVariable(req, "bike_id"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikeDetail)
                        .map(bikesService::returnData), Map.class);
    }

    public Mono<ServerResponse> addBike(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::addBike)
                        .map(bikesService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateBike(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::updateBike)
                        .map(bikesService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteBike(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::deleteBike)
                        .map(bikesService ::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchBikeModels(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikeModels)
                        .map(bikesService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchBikesWithoutLease(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikesWithoutLease)
                        .map(bikesService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchGroupDetailsByGroupId(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchGroupDetailsByGroupId)
                        .map(bikesService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchBikesByClient(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikesService.getPathVariable(req, "client_id"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikesByClient)
                        .map(bikesService::returnData), Map.class);
    }

    public Mono<ServerResponse> addBikeModel(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::addBikeModel)
                        .map(bikesService::returnData), Map.class
        );
    }

    public Mono<ServerResponse> updateBikeModel(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::updateBikeModel)
                        .map(bikesService::returnData), Map.class
        );
    }

    public Mono<ServerResponse> generatePreSign(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> bikesService.getPathVariable(row, "bike_id"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::generatePreSignedURLToUploadBikeFile)
                        .map(bikesService::returnData), Map.class);
    }

    public Mono<ServerResponse> checkUpload(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> bikesService.getPathVariable(row, "bike_id"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::checkFileUploadComplete)
                        .map(bikesService::returnData), Map.class);
    }
}
