package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.SessionResponseDto;
import helmet.bikelab.apiserver.objects.bikelabs.users.UserProfileResponse;
import helmet.bikelab.apiserver.services.SignService;
import helmet.bikelab.apiserver.services.myinfo.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MyProfileHandlers {

    private final ProfileService profileService;

    public Mono<ServerResponse> modifyProfile(ServerRequest request){
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .map(row -> profileService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(profileService::checkBikeSession)
                        .map(profileService::modifyProfile)
                        .map(profileService::returnData), Map.class);
    }

    public Mono<ServerResponse> fetchMyProfile(ServerRequest request){
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> profileService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(profileService::checkBikeSession)
                        .map(profileService::fetchMyProfile)
                        .map(profileService::returnData), UserProfileResponse.class);
    }

}