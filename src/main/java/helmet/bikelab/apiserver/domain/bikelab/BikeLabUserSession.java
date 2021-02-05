package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.BikeUserSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bike_user_sessions", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@IdClass(BikeUserSessionPK.class)
@NoArgsConstructor
public class BikeLabUserSession {

    @Id
    @Column(name = "user_no")
    private Integer bikeUserNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

    @ManyToOne
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeLabUser user;

    @Column(name = "session_key", length = 256)
    private String sessionKey;

    @Column(name = "salt", length = 256)
    private String salt;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
