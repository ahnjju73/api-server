package helmet.bikelab.apiserver.domain.riders;

import helmet.bikelab.apiserver.domain.types.RiderStatusTypes;
import helmet.bikelab.apiserver.domain.types.RiderVerifiedTypes;
import helmet.bikelab.apiserver.domain.types.converters.RiderStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.RiderVerifiedTypesConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
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
public class Riders extends OriginObject {

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

    @Column(name = "verified", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = RiderVerifiedTypesConverter.class)
    private RiderVerifiedTypes verifiedType = RiderVerifiedTypes.NOT;

    @Column(name = "verified_at", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_request_at", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime verifiedRequestAt;

    @Column(name = "verified_reject_message", columnDefinition = "MEDIUMTEXT")
    private String verifiedRejectMessage;

    @Column(name = "uuid")
    private String edpId;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "front_ssn")
    private String frontSsn;

    @Column(name = "back_ssn")
    private String backSsn;

    @OneToOne(mappedBy = "rider", fetch = FetchType.EAGER)
    private RiderInfo riderInfo;

}


