package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
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

}
