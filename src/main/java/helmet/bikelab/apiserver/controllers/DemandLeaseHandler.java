package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.responses.DemandLeaseDetailsByIdResponse;
import helmet.bikelab.apiserver.services.leases.DemandLeaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DemandLeaseHandler {

    private final DemandLeaseService demandLeaseService;

    public Mono<ServerResponse> fetchDemandLeaseById(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> demandLeaseService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> demandLeaseService.getPathVariable(row, "demand_lease_id"))
                        .map(demandLeaseService::checkBikeSession)
                        .map(demandLeaseService::fetchDemandLeaseById)
                        .map(demandLeaseService::returnData), DemandLeaseDetailsByIdResponse.class);
    }

    public Mono<ServerResponse> completedDemandLeaseById(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> demandLeaseService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> demandLeaseService.getPathVariable(row, "demand_lease_id"))
                        .map(demandLeaseService::checkBikeSession)
                        .map(demandLeaseService::completedDemandLeaseById)
                        .map(demandLeaseService::returnData), Map.class);
    }

    public Mono<ServerResponse> denyDemandLeaseById(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> demandLeaseService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> demandLeaseService.getPathVariable(row, "demand_lease_id"))
                        .map(demandLeaseService::checkBikeSession)
                        .map(demandLeaseService::denyDemandLeaseById)
                        .map(demandLeaseService::returnData), Map.class);
    }

}
