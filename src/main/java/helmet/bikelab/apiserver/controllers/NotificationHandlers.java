package helmet.bikelab.apiserver.controllers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.NotificationService;
import lombok.RequiredArgsConstructor;
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
                        .map(notificationService::returnData), Map.class);
    }
}
