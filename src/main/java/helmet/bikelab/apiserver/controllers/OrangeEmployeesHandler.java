package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.services.employees.OrangeEmployeesService;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

public class OrangeEmployeesHandler {

    OrangeEmployeesService service;

    public Mono<ServerResponse> fetchListOfEmployee(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> service.makeSessionRequest(serverRequest, BikeSessionRequest.class))
                .subscribeOn(Schedulers.elastic())
                .map(service::checkBikeSession)
                .map(service::fetchListOfEmployee)
                .map(service::returnData), Map.class);
    }

    public Mono<ServerResponse> addEmployee(ServerRequest serverRequest) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(()-> service.makeSessionRequest(serverRequest, BikeSessionRequest.class))
                .subscribeOn(Schedulers.elastic())
                .map(service::checkBikeSession)
                .map(service::addEmployee)
                .map(service::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteEmployee(ServerRequest serverRequest){
        return ServerResponse.ok().body(
                Mono.fromSupplier(()-> service.makeSessionRequest(serverRequest, BikeSessionRequest.class))
                .subscribeOn(Schedulers.elastic())
                .map(service::checkBikeSession)
                .map (service::deleteEmployee)
                .map(service::returnData),Map.class);
    }

//    public Mono<ServerResponse> modifyEmployeeInfoByManager(ServerRequest serverRequest) {
//
//    }
//
//    public Mono<ServerResponse> fireEmployee(ServerRequest serverRequest) {
//
//    }
//
//    public Mono<ServerResponse> fetchEmployeeInfoByUserNo(ServerRequest serverRequest) {
//
//    }
}
