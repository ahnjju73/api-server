package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.responses.BikeInsuranceInfoResponse;
import helmet.bikelab.apiserver.objects.responses.BikeInsuranceListResponse;
import helmet.bikelab.apiserver.services.bikes.BikesInsuranceService;
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
public class BikeInsurancesHandler {
    private final BikesInsuranceService bikesInsuranceService;

    public Mono<ServerResponse> getBikeInsurances(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesInsuranceService.makeSessionRequest(request, BikeSessionRequest.class))
                    .subscribeOn(Schedulers.elastic())
                    .map(bikesInsuranceService::checkBikeSession)
                    .map(bikesInsuranceService::getBikeInsurances)
                    .map(bikesInsuranceService::returnData), BikeInsuranceListResponse.class);
    }

    public Mono<ServerResponse> getBikeInsuranceDetails(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesInsuranceService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::getBikeInsuranceDetails)
                        .map(bikesInsuranceService::returnData), BikeInsuranceInfoResponse.class);
    }

    public Mono<ServerResponse> addNewBikeInsurance(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesInsuranceService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::addNewBikeInsurance)
                        .map(bikesInsuranceService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateBikeInsurance(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesInsuranceService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::updateBikeInsurance)
                        .map(bikesInsuranceService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteBikeInsurance(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesInsuranceService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::deleteBikeInsurance)
                        .map(bikesInsuranceService::returnData), Map.class);
    }

    public Mono<ServerResponse> renewBikeInsurance(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesInsuranceService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::renewBikeInsurance)
                        .map(bikesInsuranceService::returnData), Map.class);
    }

    public Mono<ServerResponse> setBikeInsuranceToBike(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesInsuranceService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::setBikeInsuranceToBike)
                        .map(bikesInsuranceService::returnData), Map.class);
    }


    public Mono<ServerResponse> removeBikeInsuranceToBike(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesInsuranceService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::removeBikeInsuranceToBike)
                        .map(bikesInsuranceService::returnData), Map.class);
    }

    public Mono<ServerResponse> checkIfEnableBikeInsuranceToBike(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesInsuranceService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesInsuranceService::checkBikeSession)
                        .map(bikesInsuranceService::checkIfEnableBikeInsuranceToBike)
                        .map(bikesInsuranceService::returnData), BikeInsuranceListResponse.class);
    }

}
