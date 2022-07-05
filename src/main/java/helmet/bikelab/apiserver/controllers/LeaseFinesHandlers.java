package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.responses.FetchFineDetailResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.leases.FineService;
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
public class LeaseFinesHandlers {

    private final FineService fineService;

    public Mono<ServerResponse> fetchFines(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> fineService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(fineService::checkBikeSession)
                        .map(fineService::fetchFineList)
                        .map(fineService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchFineDetail(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> fineService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(row -> fineService.getPathVariable(row, "fine_id"))
                        .subscribeOn(Schedulers.elastic())
                        .map(fineService::checkBikeSession)
                        .map(fineService::fetchFineDetail)
                        .map(fineService::returnData), FetchFineDetailResponse.class);
    }

    public Mono<ServerResponse> addFine(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> fineService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(fineService::checkBikeSession)
                        .map(fineService::addFine)
                        .map(fineService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateFine(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> fineService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> fineService.getPathVariable(row, "fine_id"))
                        .map(fineService::checkBikeSession)
                        .map(fineService::updateFine)
                        .map(fineService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteFine(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> fineService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(row -> fineService.getPathVariable(row, "fine_id"))
                        .subscribeOn(Schedulers.elastic())
                        .map(fineService::checkBikeSession)
                        .map(fineService::deleteFine)
                        .map(fineService::returnData), Map.class);
    }

    public Mono<ServerResponse> generatePresignedUrl(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> fineService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(fineService::checkBikeSession)
                        .map(fineService::generatePresignedUrl)
                        .map(fineService::returnData), PresignedURLVo.class);
    }

    public Mono<ServerResponse> fetchAttachments(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> fineService.makeSessionRequest(request, BikeSessionRequest.class))
                        .map(row -> fineService.getPathVariable(row, "fine_id"))
                        .subscribeOn(Schedulers.elastic())
                        .map(fineService::checkBikeSession)
                        .map(fineService::fetchAttachments)
                        .map(fineService::returnData), List.class);
    }


    public Mono<ServerResponse> addFineAttachment(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> fineService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(fineService::checkBikeSession)
                        .map(fineService::addAttachments)
                        .map(fineService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteFineAttachment(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> fineService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> fineService.getPathVariable(row, "fine_id"))
                        .map(fineService::checkBikeSession)
                        .map(fineService::deleteAttachment)
                        .map(fineService::returnData), Map.class);
    }

}