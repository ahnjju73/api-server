package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bike_user_info", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
public class BikeLabUserInfo {

    @Id
    @Column(name = "user_no", length = 21)
    private Integer bikeUserNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeLabUser bikeUser;

}
