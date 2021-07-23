package helmet.bikelab.apiserver.domain.riders;

import helmet.bikelab.apiserver.domain.types.RiderStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.RiderStatusTypesConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "riders", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class Riders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rider_no")
    private Integer riderNo;

    @Column(name = "rider_id", length = 21, unique = true, nullable = false)
    private String riderId;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = RiderStatusTypesConverter.class)
    private RiderStatusTypes status = RiderStatusTypes.PENDING;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "phone", unique = true, nullable = false, length = 45)
    private String phone;

    @Column(name = "notification_token", length = 512)
    private String notificationToken;

    @Column(name = "created_at", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
