package helmet.bikelab.apiserver.domain.bikelab;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class BikeUserInfo {

    @Id
    @Column(name = "user_no", length = 21)
    private Integer bikeUserNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeUser bikeUser;

    @Column(name = "name", length = 45)
    private String name;

    @Column(name = "phone", length = 45)
    private String phone;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    public void setBikeUser(BikeUser bikeUser) {
        this.bikeUser = bikeUser;
        this.bikeUserNo = bikeUser.getUserNo();
    }
}
