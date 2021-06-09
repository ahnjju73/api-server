package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.leases.LeasePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

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

    public Mono<ServerResponse> unpaidExcelDownload(ServerRequest request) {
        BikeSessionRequest bikeSessionRequest = leasePaymentService.makeSessionRequest(request, BikeSessionRequest.class);
        File excel = leasePaymentService.unpaidExcelDownload(bikeSessionRequest);
        return ServerResponse.ok()
                .header(CONTENT_DISPOSITION, "attachment;filename=test.xlsx")
                .header(CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body((p, a) -> {
                    ZeroCopyHttpOutputMessage resp = (ZeroCopyHttpOutputMessage) p;
                    return resp.writeWith(excel, 0, excel.length());
                }).doFinally(a -> excel.deleteOnExit());
    }

    public Mono<ServerResponse> payLeaseWithExcel(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> leasePaymentService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(leasePaymentService::checkBikeSession)
                        .map(leasePaymentService::payWithExcel)
                        .map(leasePaymentService::returnData), Map.class);
    }
}
