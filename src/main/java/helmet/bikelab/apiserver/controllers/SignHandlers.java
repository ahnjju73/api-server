package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.objects.SessionResponseDto;
import helmet.bikelab.apiserver.services.SignService;
import helmet.bikelab.apiserver.services.internal.BikeSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SignHandlers {

    private final SignService signService;

    public Mono<ServerResponse> addBikeUser(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> signService.makeSessionRequest(request, row, BikeSessionService.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(signService::addBikeUser)
                        .map(signService::returnData), Map.class);
    }

    public Mono<ServerResponse> signInUser(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> signService.makeSessionRequest(request, row, BikeSessionService.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(signService::signIn)
                        .map(signService::returnData), SessionResponseDto.class);
    }

    public Mono<ServerResponse> helloWorld(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> signService.makeSessionRequest(request, BikeSessionService.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(signService::checkBikeSession)
                        .map(signService::helloWorldAdmin)
                        .map(signService::returnData), SessionResponseDto.class);
    }

    public Mono<ServerResponse> signOut(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> signService.makeSessionRequest(request, BikeSessionService.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(signService::checkBikeSession)
                        .map(signService::signOut)
                        .map(signService::returnData), Map.class);
    }

}