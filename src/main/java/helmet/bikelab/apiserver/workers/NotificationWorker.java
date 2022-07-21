package helmet.bikelab.apiserver.workers;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.Notifications;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.objects.requests.NewNotificationRequest;
import helmet.bikelab.apiserver.repositories.NotificationsRepository;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationWorker extends OriginObject {

    private final NotificationsRepository notificationsRepository;

    public Notifications setNotification(NewNotificationRequest request){
        Notifications result = new Notifications();
        result.setTitle(request.getTitle());
        result.setContent(request.getContent());
        List<ImageVo> imageCollect = request.getImageList().stream().map(elm -> {
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            String fileKey = "notification_images/" + elm.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(elm.getBucket(), elm.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            ImageVo notfiImages = new ImageVo(MediaTypes.IMAGE, elm.getFilename(), fileKey);
            return notfiImages;
        }).collect(Collectors.toList());
        List<ImageVo> attachmentCollect = request.getAttachmentList().stream().map(elm -> {
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

}
