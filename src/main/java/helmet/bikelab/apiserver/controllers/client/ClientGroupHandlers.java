package helmet.bikelab.apiserver.controllers.client;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.clients.ClientGroupService;
import helmet.bikelab.apiserver.utils.MultiFiles;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

import static reactor.core.scheduler.Schedulers.parallel;

@Component
@RequiredArgsConstructor
public class ClientGroupHandlers {

    private final ClientGroupService groupService;
    private final MultiFiles multiFiles;

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


    public Mono<ServerResponse> forceDeleteClientGroup(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> groupService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(groupService::checkBikeSession)
                        .map(groupService::forceDeleteGroup)
                        .map(groupService::returnData), Map.class);
    }

    public Mono<ServerResponse> uploadExcel(ServerRequest request){
        return request.body(BodyExtractors.toMultipartData())
                .flatMap(m -> multiFiles.multipartFile(m, "excel"))
                .map(m -> groupService.makeSessionRequest(request, m, BikeSessionRequest.class))
                .map(row -> groupService.checkBikeSession(row))
                .flatMap(sessionRequest ->
                        ServerResponse.ok().body(
                                Mono.just(groupService.uploadExcel(sessionRequest))
                                        .map(groupService::returnData), Map.class).subscribeOn(parallel())
                );
    }

}
