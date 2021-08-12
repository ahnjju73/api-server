package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikeModelByIdRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.bikes.BikeModelService;
import helmet.bikelab.apiserver.services.bikes.BikesService;
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

    public Mono<ServerResponse> fetchModelManufacturer(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::fetchModelManufacturer)
                        .map(bikeModelService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchModelManufacturerByCode(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeModelService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> bikeModelService.getPathVariable(req, "code"))
                        .map(bikeModelService::checkBikeSession)
                        .map(bikeModelService::fetchModelManufacturerByCode)
                        .map(bikeModelService::returnData), CommonCodeBikes.class);
    }

}
