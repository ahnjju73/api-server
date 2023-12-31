package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.bikes.BikePartsService;
import helmet.bikelab.apiserver.services.bikes.PartsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final PartsService partsTypeService;

    public Mono<ServerResponse> doSavePartType(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsTypeService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::doSavePartType)
                        .map(partsTypeService::returnData), Map.class);
    }

    public Mono<ServerResponse> updatePartType(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsTypeService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::updatePartType)
                        .map(partsTypeService::returnData), Map.class);
    }

    public Mono<ServerResponse> doSavePartsCode(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsTypeService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::doSavePartsCode)
                        .map(partsTypeService::returnData), Map.class);
    }

    public Mono<ServerResponse> updatePartsCode(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsTypeService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::updatePartsCode)
                        .map(partsTypeService::returnData), Map.class);
    }

    public Mono<ServerResponse> deletePartsCode(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsTypeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::deletePartsCode)
                        .map(partsTypeService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchPartsTypeList(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsTypeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::fetchPartsTypeList)
                        .map(partsTypeService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchParsCodeListByCondition(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsTypeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::fetchParsCodeListByCondition)
                        .map(partsTypeService::returnData), Page.class);
    }

    public Mono<ServerResponse> fetchPartsCodeList(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsTypeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::fetchPartsCodeList)
                        .map(partsTypeService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchPartsByID(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchPartsByID)
                        .map(partsService::returnData), Parts.class);
    }

    public Mono<ServerResponse> fetchParts(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchParts)
                        .map(partsService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> moveParsCodeToAnotherType(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsTypeService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::moveParsCodeToAnotherType)
                        .map(partsTypeService::returnData), Map.class);
    }

    public Mono<ServerResponse> deletePartsType(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsTypeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(partsTypeService::checkBikeSession)
                        .map(partsTypeService::deletePartsType)
                        .map(partsTypeService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchPartsCodes(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchPartsCodes)
                        .map(partsService::returnData), Parts.class);
    }

    public Mono<ServerResponse> fetchPartsTypes(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchPartsTypes)
                        .map(partsService::returnData), List.class);
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

    public Mono<ServerResponse> updatePartsByIdAndCarModel(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::updatePartsByIdAndCarModel)
                        .map(partsService::returnData), Map.class);
    }

    public Mono<ServerResponse> generatePresignedUrl(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::generatePresignedUrl)
                        .map(partsService::returnData), PresignedURLVo.class);
    }

    public Mono<ServerResponse> deletePartsImage(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsService::checkBikeSession)
                        .map(partsService::deletePartsImage)
                        .map(partsService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchPartImageByPartsId(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchPartImageByPartsId)
                        .map(partsService::returnData), List.class);
    }

    public Mono<ServerResponse> addNewPartsImage(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::addNewPartsImage)
                        .map(partsService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchCommonWorkingPriceList(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsService::checkBikeSession)
                        .map(partsService::fetchCommonWorkingPriceList)
                        .map(partsService::returnData), List.class);
    }

    public Mono<ServerResponse> getPartsPriceHistory(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> partsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(partsService::checkBikeSession)
                        .map(partsService::getPartsPriceHistory)
                        .map(partsService::returnData), List.class);
    }

    public Mono<ServerResponse> uploadNewParts(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::uploadNewParts)
                        .map(partsService::returnData), Map.class);
    }

    public Mono<ServerResponse> uploadModelParts(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::uploadModelParts)
                        .map(partsService::returnData), Map.class);
    }

    public Mono<ServerResponse> uploadModelPartsPrice(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> partsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(partsService::checkBikeSession)
                        .map(partsService::uploadModelPartsPrice)
                        .map(partsService::returnData), Map.class);
    }
}
