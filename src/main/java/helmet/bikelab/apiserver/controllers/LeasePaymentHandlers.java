package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.leases.LeasePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeasePaymentHandlers {
    private final LeasePaymentService leasePaymentService;

    public Mono<ServerResponse> fetchLeases(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leasePaymentService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leasePaymentService::checkBikeSession)
                        .map(leasePaymentService::fetchUnpaidLeases)
                        .map(leasePaymentService::returnData), Map.class);
    }

    public Mono<ServerResponse> payLease(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasePaymentService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> leasePaymentService.getPathVariable(row, "lease_id"))
                        .map(leasePaymentService::checkBikeSession)
                        .map(leasePaymentService::payLeaseFee)
                        .map(leasePaymentService::returnData), Map.class);
    }

}
