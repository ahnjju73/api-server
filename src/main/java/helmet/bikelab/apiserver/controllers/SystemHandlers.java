package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.SessionResponseDto;
import helmet.bikelab.apiserver.services.SignService;
import helmet.bikelab.apiserver.services.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SystemHandlers {

    private final SystemService systemHandlers;

    public Mono<ServerResponse> fetchMyLeftMenu(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> systemHandlers.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(systemHandlers::checkBikeSession)
                        .map(systemHandlers::fetchMyLeftMenu)
                        .map(systemHandlers::returnData), List.class);
    }

}