package helmet.bikelab.apiserver.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.Notifications;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.requests.NewNotificationRequest;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.repositories.NotificationsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.NotificationWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService extends SessService {

    private final NotificationWorker notificationWorker;
    private final CommonWorker commonWorker;
    private final NotificationsRepository notificationsRepository;

    public BikeSessionRequest fetchNotifications(BikeSessionRequest request) {
        Map param = request.getParam();
        String notificationType = (String)param.get("notification_type");
        PageableRequest pageableRequest = map(request.getParam(), PageableRequest.class);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        Page<Notifications> notifications = notificationWorker.getNotifications(notificationType, pageable);
        request.setResponse(notifications);
        return request;
    }

    public BikeSessionRequest makeNotification(BikeSessionRequest request) {
        NewNotificationRequest notificationRequest = map(request.getParam(), NewNotificationRequest.class);
        notificationRequest.checkValidation();
        Notifications notifications = notificationWorker.setNotification(notificationRequest);
        notificationsRepository.save(notifications);
        notificationWorker.saveNotificationType(notifications.getNotificationNo(), notificationRequest.getNotificationTypes());
        return request;
    }

    public BikeSessionRequest generatePresignedUrl(BikeSessionRequest request){
        Map param = request.getParam();
        String filename = (String)param.get("filename");
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(filename, null);
        request.setResponse(presignedURLVo);
        return request;
    }
}
