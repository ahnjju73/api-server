package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.riders.RiderInsurances;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.insurance.InsurancesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    public Mono<ServerResponse> fetchRiderInsurances(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insurancesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::fetchRiderInsurances)
                        .map(insurancesService::returnData), Page.class);
    }

    public Mono<ServerResponse> addRiderInsurance(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insurancesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::addRiderInsurance)
                        .map(insurancesService::returnData), ResponseListDto.class);
    }
    public Mono<ServerResponse> fetchRiderInsuranceDetail(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insurancesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> insurancesService.getPathVariable(req, "rider_ins_id"))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService:: fetchRiderInsuranceDetail)
                        .map(insurancesService::returnData), RiderInsurances.class);
    }

    public Mono<ServerResponse> updateRiderInsurance(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insurancesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(req -> insurancesService.getPathVariable(req, "rider_ins_id"))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::updateRiderInsurance)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateRiderInsuranceDetail(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insurancesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService::updateRiderInsuranceDtls)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteRiderInsurance(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insurancesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> insurancesService.getPathVariable(req, "rider_ins_id"))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService:: deleteRiderInsurance)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> renewRiderInsurance(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insurancesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(req -> insurancesService.getPathVariable(req, "rider_ins_id"))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService:: renewInsurance)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> confirmInsurance(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> insurancesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> insurancesService.getPathVariable(req, "rider_ins_id"))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService:: confirmInsurance)
                        .map(insurancesService::returnData), Map.class);
    }

    public Mono<ServerResponse> generatePresignedUrl(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> insurancesService.makeSessionRequest(request, row , BikeSessionRequest.class))
                        .map(insurancesService::checkBikeSession)
                        .map(insurancesService:: generatePresignedUrl)
                        .map(insurancesService::returnData), PresignedURLVo.class);
    }
}
