package helmet.bikelab.apiserver.domain.bikelab;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeUserStatusTypesConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bike_user_mst", catalog = SESSION.SCHEME_SERVICE)
public class BikeUser {

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
    private BikeUserStatusTypes userStatusTypes = BikeUserStatusTypes.PENDING;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "bikeUser", optional = false)
    private BikeUserInfo bikeUserInfo;

    @JsonIgnore
    @OneToOne(mappedBy = "bikeUser", optional = false)
    private BikeUserPassword bikeUserPassword;

}

