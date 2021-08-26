package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.ExpenseDto;
import helmet.bikelab.apiserver.services.leases.LeaseExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeaseExpenseHandler {
    private final LeaseExpenseService leaseExpenseService;

    public Mono<ServerResponse> fetchExpenses(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leaseExpenseService.makeSessionRequest(request, BikeSessionRequest.class))
                            .subscribeOn(Schedulers.elastic())
                            .map(leaseExpenseService::checkBikeSession)
                            .map(leaseExpenseService::fetchLeaseExpenses)
                            .map(leaseExpenseService::returnData), ExpenseDto.class);
    }

    public Mono<ServerResponse> addExpense(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> leaseExpenseService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leaseExpenseService::checkBikeSession)
                        .map(leaseExpenseService::addLeaseExpense)
                        .map(leaseExpenseService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateExpense(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> leaseExpenseService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leaseExpenseService::checkBikeSession)
                        .map(leaseExpenseService::updateLeaseExpense)
                        .map(leaseExpenseService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteExpense(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> leaseExpenseService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(leaseExpenseService::checkBikeSession)
                        .map(leaseExpenseService::deleteLeaseExpense)
                        .map(leaseExpenseService::returnData), Map.class);
    }
}
