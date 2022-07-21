package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.converters.ImageVoConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "notifications")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Notifications {

    @Id
    @Column(name = "notification_no")
    private Integer notificationNo;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    @OneToMany(mappedBy = "notifications", fetch = FetchType.EAGER)
    private List<NotificationTargets> notificationTargets;

    @JsonIgnore
    @Column(name = "images", columnDefinition = "json")
    @Convert(converter = ImageVoConverter.class)
    private List<ImageVo> imageList;

    @Column(name = "images", columnDefinition = "json", insertable = false, updatable = false)
    private String images;

    @JsonIgnore
    @Column(name = "attachments", columnDefinition = "json")
    @Convert(converter = ImageVoConverter.class)
    private List<ImageVo> attachmentList;

    @Column(name = "attachments", columnDefinition = "json", insertable = false, updatable = false)
    private String attachments;

    @Column(name = "start_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime startAt;

    @Column(name = "end_at", columnDefinition = "DATETIME")
    private LocalDateTime endAt;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
