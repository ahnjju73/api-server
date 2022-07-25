package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.NotificationTargetPK;
import helmet.bikelab.apiserver.domain.types.NotificationTypes;
import helmet.bikelab.apiserver.domain.types.converters.NotificationTypeConverter;
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

    @JsonIgnore
    @JoinColumn(name = "notification_no", insertable = false, updatable = false)
    @ManyToOne
    private Notifications notifications;

    @Id
    @Column(name = "notification_type", columnDefinition = "ENUM")
    @Convert(converter = NotificationTypeConverter.class)
    private NotificationTypes notificationType;

    @Column(name = "notification_type", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String notificationCode;
}
