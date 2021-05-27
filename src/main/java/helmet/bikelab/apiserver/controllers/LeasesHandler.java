package helmet.bikelab.apiserver.controllers;

import com.amazonaws.services.opsworkscm.model.Server;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.leases.LeasesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeasesHandler {
    private final LeasesService leasesService;

    public Mono<ServerResponse> fetchLeases(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::fetchLeases)
                        .map(leasesService::returnData), Map.class);
    }
    public Mono<ServerResponse> fetchLease(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> leasesService.getPathVariable(req, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::fetchDetailLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> addLease(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::addLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateLease(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasesService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> leasesService.getPathVariable(row, "lease_id"))
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::updateLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> confirmApplication(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::confirmLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> submitApplication(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::pendingLease)
                        .map(leasesService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteInsurance(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasesService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasesService::checkBikeSession)
                        .map(leasesService::rejectLease)
                        .map(leasesService::returnData), Map.class);
    }
}
