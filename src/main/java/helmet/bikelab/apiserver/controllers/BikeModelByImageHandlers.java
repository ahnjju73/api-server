package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.SectionAxis;
import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.objects.responses.SectionDetailResponse;
import helmet.bikelab.apiserver.services.bikes.BikeModelByImageService;
import helmet.bikelab.apiserver.services.bikes.BikesService;
import helmet.bikelab.apiserver.utils.MultiFiles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

import static reactor.core.scheduler.Schedulers.parallel;

@Component
@RequiredArgsConstructor
public class BikeModelByImageHandlers {

    private final BikeModelByImageService bikeModelByImageService;

    public Mono<ServerResponse> generatePreSignedUrl(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::generatePreSignedUrl)
                        .map(bikeModelByImageService::returnData), PresignedURLVo.class);
    }

    public Mono<ServerResponse> doSaveSection(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::doSaveSection)
                        .map(bikeModelByImageService::returnData), Sections.class);
    }

    public Mono<ServerResponse> doSaveSectionAxis(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::doSaveSectionAxis)
                        .map(bikeModelByImageService::returnData), SectionAxis.class);
    }

    public Mono<ServerResponse> doUpdateSectionAxis(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::doUpdateSectionAxis)
                        .map(bikeModelByImageService::returnData), SectionAxis.class);
    }

    public Mono<ServerResponse> handleSectionAxisParts(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::handleSectionAxisParts)
                        .map(bikeModelByImageService::returnData), Map.class);
    }


    public Mono<ServerResponse> fetchSectionsByModel(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelByImageService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::fetchSectionsByModel)
                        .map(bikeModelByImageService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchSectionsDetail(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelByImageService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.getPathVariable(row, "section_no"))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::fetchSectionAxisDetail)
                        .map(bikeModelByImageService::returnData), SectionDetailResponse.class);
    }

    public Mono<ServerResponse> fetchPartsByAxis(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelByImageService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::fetchPartsByAxis)
                        .map(bikeModelByImageService::returnData), List.class);
    }

    public Mono<ServerResponse> deleteAxis(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::deleteSectionAxis)
                        .map(bikeModelByImageService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteSection(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> bikeModelByImageService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(bikeModelByImageService::checkBikeSession)
                        .map(bikeModelByImageService::deleteSection)
                        .map(bikeModelByImageService::returnData), Map.class);
    }

}
