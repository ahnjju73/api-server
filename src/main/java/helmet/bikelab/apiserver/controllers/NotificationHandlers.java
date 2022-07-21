package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationHandlers {

    private final NotificationService notificationService;

    public Mono<ServerResponse> fetchNotifications(ServerRequest request) {
        return ServerResponse.ok().body(
                Mono.fromSupplier(() -> notificationService.makeSessionRequest(request, BikeSessionRequest.class))
                        .subscribeOn(Schedulers.elastic())
                        .map(notificationService::checkBikeSession)
                        .map(notificationService::fetchNotifications)
                        .map(notificationService::returnData), Page.class);
    }

    public Mono<ServerResponse> makeNotification(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> notificationService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(notificationService::checkBikeSession)
                        .map(notificationService::makeNotification)
                        .map(notificationService::returnData), Map.class);
    }

    public Mono<ServerResponse> generatePresignedUrl(ServerRequest request) {
        return ServerResponse.ok().body(
                request.bodyToMono(Map.class)
                        .subscribeOn(Schedulers.elastic())
                        .map(row -> notificationService.makeSessionRequest(request, row, BikeSessionRequest.class))
                        .map(notificationService::checkBikeSession)
                        .map(notificationService::generatePresignedUrl)
                        .map(notificationService::returnData), PresignedURLVo.class);
    }
}
