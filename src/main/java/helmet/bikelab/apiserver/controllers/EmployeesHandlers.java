package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.bikelabs.todo.BikeUserTodoDto;
import helmet.bikelab.apiserver.services.BikeUserTodoService;
import helmet.bikelab.apiserver.services.employees.EmployeesService;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmployeesHandlers {

    private final EmployeesService adminEmployeeService;
    private final BikeUserTodoService bikeUserTodoService;

    public Mono<ServerResponse> fetchTodoSummery(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> bikeUserTodoService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(bikeUserTodoService::checkBikeSession)
                        .map(bikeUserTodoService::fetchTodoSummery)
                        .map(bikeUserTodoService::returnData), BikeUserTodoDto.class);
    }

    public Mono<ServerResponse> fetchListOfEmployee(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> adminEmployeeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(adminEmployeeService::checkBikeSession)
                        .map(adminEmployeeService::fetchListOfEmployee)
                        .map(adminEmployeeService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchMyInfo(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> adminEmployeeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(adminEmployeeService::checkBikeSession)
                        .map(adminEmployeeService::fetchMyInfo)
                        .map(adminEmployeeService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateMyInfo(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> adminEmployeeService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(adminEmployeeService::checkBikeSession)
                        .map(adminEmployeeService::updateMyInfo)
                        .map(adminEmployeeService::returnData), Map.class);
    }

    public Mono<ServerResponse> modifyEmployeeInfoByManager(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> adminEmployeeService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(row -> adminEmployeeService.getPathVariable(row, "user_no"))
                        .map(adminEmployeeService::checkBikeSession)
                        .map(adminEmployeeService::modifyEmployeeInfoByManager)
                        .map(adminEmployeeService::returnData), Map.class);
    }

    public Mono<ServerResponse> fireEmployee(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> adminEmployeeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(adminEmployeeService::checkBikeSession)
                        .map(adminEmployeeService::fireEmployee)
                        .map(adminEmployeeService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchEmployeeInfoByUserNo(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> adminEmployeeService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> adminEmployeeService.getPathVariable(req, "user_no"))
                        .map(adminEmployeeService::checkBikeSession)
                        .map(adminEmployeeService::fetchEmployeeInfoByUserNo)
                        .map(adminEmployeeService::returnData), Map.class);
    }

}