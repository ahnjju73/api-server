package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.bikes.BikePartsService;
import helmet.bikelab.apiserver.services.bikes.DiagramService;
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
public class DiagramHandlers {

    private final DiagramService diagramService;

    public Mono<ServerResponse> addNewDiagram(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> diagramService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(diagramService::checkBikeSession)
                        .map(diagramService::addNewDiagram)
                        .map(diagramService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateDiagram(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> diagramService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(diagramService::checkBikeSession)
                        .map(diagramService::updateDiagram)
                        .map(diagramService::returnData), Map.class);
    }

    public Mono<ServerResponse> generatePreSigned(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> diagramService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(diagramService::checkBikeSession)
                        .map(diagramService::generatePreSigned)
                        .map(diagramService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateImageByDiagramId(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> diagramService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(diagramService::checkBikeSession)
                        .map(diagramService::updateImageByDiagramId)
                        .map(diagramService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteDiagram(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> diagramService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(diagramService::checkBikeSession)
                        .map(diagramService::deleteDiagram)
                        .map(diagramService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchDiagramDetailsById(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> diagramService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(diagramService::checkBikeSession)
                        .map(diagramService::fetchDiagramDetailsById)
                        .map(diagramService::returnData), Page.class);
    }

    public Mono<ServerResponse> deleteImageByDiagramId(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> diagramService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(diagramService::checkBikeSession)
                        .map(diagramService::deleteImageByDiagramId)
                        .map(diagramService::returnData), List.class);
    }

}
