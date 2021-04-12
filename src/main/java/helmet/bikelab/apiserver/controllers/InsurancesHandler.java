package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.insurance.InsurancesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InsurancesHandler {
    private final InsurancesService insurancesService;

    public Mono<ServerResponse> fetchInsurance(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insurancesService.makeSessionRequest(request, BikeSessionRequest.class))
                    .subscribeOn(Schedulers.elastic())
                    .map(insurancesService::checkBikeSession)
                    .map(insurancesService::fetchInsurances)
                    .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> addInsurance(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insurancesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::addInsurance)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateInsurance(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insurancesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::updateInsurance)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteInsurance(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(()-> insurancesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::deleteInsurance)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchInsuranceOption(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insurancesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::fetchInsuranceOption)
                        .map(insurancesService::returnData), Map.class);
    }

}
