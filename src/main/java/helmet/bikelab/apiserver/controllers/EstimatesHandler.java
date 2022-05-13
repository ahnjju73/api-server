package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.responses.EstimateByIdResponse;
import helmet.bikelab.apiserver.objects.responses.FetchUnpaidEstimateResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.EstimateService;
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
public class EstimatesHandler {

    private final EstimateService estimateService;

    public Mono<ServerResponse> fetchClientsWithUnpaidEstimates(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> estimateService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(estimateService::checkBikeSession)
                        .map(estimateService::fetchClientsWithUnpaidEstimate)
                        .map(estimateService::returnData), List.class);
    }

    public Mono<ServerResponse> payUnpaidEstimatesByClients(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> estimateService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(req -> estimateService.getPathVariable(req, "client_id"))
                        .map(estimateService::checkBikeSession)
                        .map(estimateService::payUnpaidEstimatesByClients)
                        .map(estimateService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchUnpaidEstimates(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> estimateService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(estimateService::checkBikeSession)
                        .map(estimateService::fetchUnpaidEstimates)
                        .map(estimateService::returnData), FetchUnpaidEstimateResponse.class);
    }

    public Mono<ServerResponse> fetchEstimate(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> estimateService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> estimateService.getPathVariable(req, "estimate_id"))
                        .map(estimateService::checkBikeSession)
                        .map(estimateService::fetchEstimateDetail)
                        .map(estimateService::returnData), EstimateByIdResponse.class);
    }

    public Mono<ServerResponse> fetchEstimateList(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> estimateService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(estimateService::checkBikeSession)
                        .map(estimateService::fetchEstimates)
                        .map(estimateService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> excelDownloadEstimates(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> estimateService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(estimateService::checkBikeSession)
                        .map(estimateService::excelDownloadEstimates)
                        .map(estimateService::returnData), List.class);
    }

    public Mono<ServerResponse> fetchReviewList(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> estimateService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(estimateService::checkBikeSession)
                        .map(estimateService::fetchReviewsByShop)
                        .map(estimateService::returnData), Page.class);
    }
}
