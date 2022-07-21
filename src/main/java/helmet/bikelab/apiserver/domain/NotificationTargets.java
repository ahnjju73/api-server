package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.NotificationTargetPK;
import helmet.bikelab.apiserver.domain.types.NotificationTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@IdClass(NotificationTargetPK.class)
@Table(name = "notification_targets")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NotificationTargets {

    @Id
    @Column(name = "notification_no")
    private Integer notificationNo;

    @Id
    @Column(name = "notification_type", columnDefinition = "ENUM")
    private NotificationTypes notificationType;

    @Column(name = "notification_type", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String notificationCode;
}
