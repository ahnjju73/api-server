package helmet.bikelab.apiserver.workers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.NotificationTargets;
import helmet.bikelab.apiserver.domain.Notifications;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.domain.types.NotificationTypes;
import helmet.bikelab.apiserver.objects.requests.NewNotificationRequest;
import helmet.bikelab.apiserver.repositories.NotificationTargetRepository;
import helmet.bikelab.apiserver.repositories.NotificationsRepository;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.services.internal.Workspace;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationWorker extends Workspace {

    private final NotificationsRepository notificationsRepository;
    private final NotificationTargetRepository notificationTargetRepository;


    public Notifications setNotification(NewNotificationRequest request, Integer notificationNo){
        Notifications result = new Notifications();
        if(bePresent(notificationNo))
            result = notificationsRepository.findById(notificationNo).get();
        result.setTitle(request.getTitle());
        result.setContent(request.getContent());
        List<ImageVo> imageList = result.getImageList();
        List<ImageVo> attachmentList = result.getAttachmentList();
        List<ImageVo> imageCollect = request.getImageList().stream().map(elm -> {
            if(bePresent(elm.getId()))
                return findById(imageList, elm.getId());
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            String fileKey = "notification_images/" + elm.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(elm.getBucket(), elm.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            ImageVo notfiImages = new ImageVo(MediaTypes.IMAGE, elm.getFilename(), fileKey);
            return notfiImages;
        }).collect(Collectors.toList());
        List<ImageVo> attachmentCollect = request.getAttachmentList().stream().map(elm -> {
            if(bePresent(elm.getId()))
                return findById(attachmentList, elm.getId());
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            String fileKey = "notification_attachments/" + elm.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(elm.getBucket(), elm.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            ImageVo notiAttachment = new ImageVo(null, elm.getFilename(), fileKey);
            return notiAttachment;
        }).collect(Collectors.toList());
        result.setImageList(imageCollect);
        result.setAttachmentList(attachmentCollect);
        result.setStartAt(LocalDateTime.parse(request.getStartAt()));
        result.setEndAt(LocalDateTime.parse(request.getEndAt()));
        return result;
    }

    private ImageVo findById(List<ImageVo> attachments, String id){
        for(ImageVo iv : attachments){
            if(iv.getId().equals(id)){
                return iv;
            }
        }
        return null;
    }

    public void saveNotificationType(Integer notificationNo, List<String> types){
        notificationTargetRepository.deleteAllByNotificationNo(notificationNo);
        List<NotificationTargets> targetsList = types.stream().map(elm -> {
            NotificationTargets target = new NotificationTargets();
            target.setNotificationNo(notificationNo);
            target.setNotificationType(NotificationTypes.getType(elm));
            return target;
        }).collect(Collectors.toList());
        notificationTargetRepository.saveAll(targetsList);
    }

    public Page<Notifications> getNotifications(String notificationType, Pageable pageable){
        Page<Notifications> notifications;
        if(!bePresent(notificationType)){
            notifications = notificationsRepository.findAll(pageable);
        }
        else{
            notifications = notificationsRepository.getNotificationsByType(notificationType, pageable);
        }
        return notifications;
    }

    public void deleteNotification(Integer notificationNo) {
        notificationTargetRepository.deleteAllByNotificationNo(notificationNo);
        notificationsRepository.deleteByNotificationNo(notificationNo);
    }
}
