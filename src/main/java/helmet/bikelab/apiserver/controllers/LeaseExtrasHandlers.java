package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.leases.LeaseExtraService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeaseExtrasHandlers {
    private final LeaseExtraService extraService;

    public Mono<ServerResponse> fetchExtra(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> extraService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> extraService.getPathVariable(req, "payment_id"))
                        .map(extraService::checkBikeSession)
                        .map(extraService::fetchLeaseExtrasByPaymentId)
                        .map(extraService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchExtras(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> extraService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(extraService::checkBikeSession)
                        .map(extraService::fetchLeaseExtras)
                        .map(extraService::returnData), Map.class);
    }

    public Mono<ServerResponse> addExtra(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> extraService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(extraService::checkBikeSession)
                        .map(extraService::addLeaseExtra)
                        .map(extraService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateExtra(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> extraService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> extraService.getPathVariable( row, "extra_id"))
                        .map(extraService::checkBikeSession)
                        .map(extraService::updateLeaseExtra)
                        .map(extraService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteExtra(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> extraService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(extraService::checkBikeSession)
                        .map(extraService::deleteLeaseExtra)
                        .map(extraService::returnData), Map.class);
    }
}
