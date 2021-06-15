package helmet.bikelab.apiserver.controllers.client;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.clients.ClientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClientsHandlers {
    private final ClientsService clientsService;


    public Mono<ServerResponse> fetchListOfClients(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> clientsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(clientsService::checkBikeSession)
                        .map(clientsService::fetchListOfClients)
                        .map(clientsService::returnData), ResponseListDto.class);
    }

    public Mono<ServerResponse> fetchClientDetailByClientId(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> clientsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(req -> clientsService.getPathVariable(req, "client_id"))
                        .map(clientsService::checkBikeSession)
                        .map(clientsService::fetchDetailClient)
                        .map(clientsService::returnData), Map.class);
    }

    public Mono<ServerResponse> addClient(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> clientsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(clientsService::checkBikeSession)
                        .map(clientsService::addClient)
                        .map(clientsService::returnData), Map.class);
    }

    public Mono<ServerResponse> updateClient(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> clientsService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(clientsService::checkBikeSession)
                        .map(clientsService::updateClient)
                        .map(clientsService::returnData), Map.class);
    }

    public Mono<ServerResponse> deleteClient(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> clientsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(clientsService::checkBikeSession)
                        .map(clientsService::deleteClient)
                        .map(clientsService::returnData), Map.class);
    }

    public Mono<ServerResponse> resetPassword(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> clientsService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(clientsService::checkBikeSession)
                        .map(clientsService::resetPassword)
                        .map(clientsService::returnData), Map.class);
    }
}
