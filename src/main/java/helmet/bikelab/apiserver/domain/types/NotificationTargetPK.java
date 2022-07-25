package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.types.converters.NotificationTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.io.Serializable;

public class NotificationTargetPK implements Serializable {

    @Column(name = "notification_no")
    private Integer notificationNo;

    @Column(name = "notification_type", columnDefinition = "ENUM")
    @Convert(converter = NotificationTypeConverter.class)
    private NotificationTypes notificationType;
}
