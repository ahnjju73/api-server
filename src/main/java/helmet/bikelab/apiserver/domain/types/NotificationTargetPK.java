package helmet.bikelab.apiserver.domain.types;

import javax.persistence.Column;
import java.io.Serializable;

public class NotificationTargetPK implements Serializable {

    @Column(name = "notification_no")
    private Integer notificationNo;

    @Column(name = "notification_type", columnDefinition = "ENUM")
    private NotificationTypes notificationType;
}
