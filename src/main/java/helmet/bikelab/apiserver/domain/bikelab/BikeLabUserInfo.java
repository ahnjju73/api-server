package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "srv_usr_info", catalog = SESSION.SCHEME_ADMIN)
@Getter
@Setter
@NoArgsConstructor
public class BikeLabUserInfo {

    @Id
    @Column(name = "user_no", length = 21)
    private String userNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeLabUser user;

    @Column(name = "username", length = 50)
    private String userName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "thumbnail", length = 250)
    private String thumbnail;

    @Column(name = "intro", columnDefinition = "LONGTEXT")
    private String introduction;

    @Column(name = "ins_dt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime insertedDate = LocalDateTime.now();

    @Column(name = "upt_dt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedDate = LocalDateTime.now();

}
