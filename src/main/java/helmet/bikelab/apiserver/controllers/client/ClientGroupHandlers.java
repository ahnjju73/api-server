package helmet.bikelab.apiserver.controllers.client;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.clients.ClientGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClientGroupHandlers {

    private final ClientGroupService groupService;

    public Mono<ServerResponse> fetchListOfGroup(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> groupService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(groupService::checkBikeSession)
                        .map(groupService::fetchListOfGroup)
                        .map(groupService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchClientsByGroup(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> groupService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(groupService::checkBikeSession)
                        .map(groupService::fetchClientsByGroup)
                        .map(groupService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateClientGroup(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row-> groupService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(groupService::checkBikeSession)
                        .map(groupService::updateGroupInfo)
                        .map(groupService::returnData), Map.class);
    }

    public Mono<ServerResponse> addClientGroup(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row-> groupService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(groupService::checkBikeSession)
                        .map(groupService::addNewGroup)
                        .map(groupService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteClientGroup(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> groupService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(groupService::checkBikeSession)
                        .map(groupService::deleteGroup)
                        .map(groupService::returnData), Map.class);
    }
}
