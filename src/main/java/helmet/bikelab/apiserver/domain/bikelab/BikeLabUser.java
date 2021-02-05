package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.AccountStatusConverter;
import helmet.bikelab.apiserver.domain.types.converters.BikeUserStatusTypesConverter;
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
@Table(name = "bike_user_mst", catalog = SESSION.SCHEME_SERVICE)
public class BikeLabUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Integer userNo;

    @Column(name = "user_id", length = 21, unique = true)
    private String userId;

    @Column(name = "email", length = 250, unique = true)
    private String email;

    @Column(name = "status", columnDefinition = "ENUM")
    @Convert(converter = BikeUserStatusTypesConverter.class)
    private BikeUserStatusTypes userStatusTypes;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "bikeUser", optional = false)
    private BikeLabUserInfo userInfo;

}

