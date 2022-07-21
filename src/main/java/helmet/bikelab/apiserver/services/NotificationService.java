package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;

@Service
@RequiredArgsConstructor
public class NotificationService extends SessService {

    public BikeSessionRequest fetchNotifications(BikeSessionRequest request) {

        return request;
    }
}
