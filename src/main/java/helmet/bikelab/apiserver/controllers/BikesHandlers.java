package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.bike.BikeAttachments;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.bikes.BikesService;
import helmet.bikelab.apiserver.utils.MultiFiles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static reactor.core.scheduler.Schedulers.parallel;

@Component
@RequiredArgsConstructor
public class BikesHandlers {

    private final BikesService bikesService;
    private final MultiFiles multiFiles;

    public Mono<ServerResponse> fetchHistoriesByBikeId(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikesService.getPathVariable(req, "bike_id"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchHistoriesByBikeId)
                        .map(bikesService::returnData), ResponseListDto.class);
    }

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

    public Mono<ServerResponse> fetchBikeVolumes(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikeVolumes)
                        .map(bikesService::returnData), List.class);
    }


    public Mono<ServerResponse> fetchBikeModels(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikeModels)
                        .map(bikesService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchBikeModelsByVolume(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikesService.getPathVariable(req, "volume"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikeModelsByVolume)
                        .map(bikesService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchBikesRidable(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchBikesRidable)
                        .map(bikesService::returnData), ResponseListDto.class);
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

    public Mono<ServerResponse> deleteBikeModel(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikesService.getPathVariable(req, "client_id"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::deleteBikeModel)
                        .map(bikesService::returnData), Map.class);
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
                        .map(bikesService::returnData), PresignedURLVo.class);
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

    public Mono<ServerResponse> fetchBikeFiles(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikesService.getPathVariable(req, "bike_id"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::fetchFilesByBike)
                        .map(bikesService::returnData), List.class);
    }

    public Mono<ServerResponse> deleteBikeFile(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(()-> bikesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikesService.getPathVariable(req, "bike_attachment_no"))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::deleteFile)
                        .map(bikesService::returnData), Map.class);
    }


    public Mono<ServerResponse> uploadFile(ServerRequest request){
        return request.body(BodyExtractors.toMultipartData())
                .flatMap(m -> multiFiles.multipartFile(m, "excel"))
                .map(m -> bikesService.makeSessionRequest(request, m, BikeSessionRequest.class))
                .map(row -> bikesService.checkBikeSession(row))
                .flatMap(sessionRequest ->
                        ServerResponse.ok().body(
                                Mono.just(bikesService.uploadBikePost(sessionRequest))
                                        .map(bikesService::returnData), String.class).subscribeOn(parallel())
                );
    }

    public Mono<ServerResponse> uploadExcelToAddBike(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikesService::checkBikeSession)
                        .map(bikesService::uploadExcelToAddBike)
                        .map(bikesService::returnData), Map.class);
    }


}
