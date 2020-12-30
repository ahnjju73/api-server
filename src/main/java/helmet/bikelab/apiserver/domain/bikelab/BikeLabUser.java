package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.AccountStatusConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "srv_usr_mst", catalog = SESSION.SCHEME_ADMIN)
public class BikeLabUser {

    @Id
    @Column(name = "user_no", length = 21)
    private String userNo;

    @Column(name = "email", length = 250, unique = true)
    private String email;

    @Column(name = "acc_stat", columnDefinition = "ENUM")
    @Convert(converter = AccountStatusConverter.class)
    private AccountStatusTypes accountStatusTypes;

    @Column(name = "ins_dt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime insertedDate = LocalDateTime.now();

    @Column(name = "upt_dt", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedDate = LocalDateTime.now();

    @OneToOne(mappedBy = "user", optional = false)
    private BikeLabUserInfo userInfo;

}

