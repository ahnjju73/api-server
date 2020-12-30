package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "srv_usr_sess", catalog = SESSION.SCHEME_ADMIN)
@Getter
@Setter
@NoArgsConstructor
public class BikeLabUserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Integer idx;

    @Column(name = "user_no", length = 21, insertable = false, updatable = false)
    private String userNo;

    @ManyToOne
    @JoinColumn(name = "user_no")
    private BikeLabUser user;

    @Column(name = "sess_tp", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

    @Column(name = "sess_key", length = 256)
    private String sessionKey;

    @Column(name = "salt", length = 88)
    private String salt;

    @Column(name = "ins_dt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime insertedDate = LocalDateTime.now();

    @Column(name = "upt_dt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedDate = LocalDateTime.now();

}
