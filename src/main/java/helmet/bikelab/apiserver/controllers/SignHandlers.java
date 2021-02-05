package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.SessionResponseDto;
import helmet.bikelab.apiserver.services.SignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
public class SignHandlers {

    @Autowired
    private SignService signService;

    public Mono<ServerResponse> signUpUser(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> signService.makeData(request, row))
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(signService::signUp)
                        .map(signService::returnData), Map.class);
    }

    public Mono<ServerResponse> signInUser(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> signService.makeData(request, row))
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(signService::signIn), SessionResponseDto.class);
    }

    public Mono<ServerResponse> helloWorld(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> signService.makeData(request))
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(signService::checkSession)
                        .map(signService::helloWorldAdmin), SessionResponseDto.class);
    }

    public Mono<ServerResponse> signOut(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> signService.makeData(request))
                        .subscribeOn(Schedulers.boundedElastic())
                        .map(signService::checkSession)
                        .map(signService::signOut)
                        .map(signService::returnData), Map.class);
    }

}