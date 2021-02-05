package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bike_user_passwords", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
public class BikeLabUserPassword {

    @Id
    @Column(name = "user_no", length = 21)
    private Integer bikeUserNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeLabUser bikeUser;

    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "salt", length = 256)
    private String salt;

    @Column(name = "bak_password", length = 256)
    private String bakPassword;

    @Column(name = "bak_salt", length = 256)
    private String bakSalt;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
