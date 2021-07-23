package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "rider_info", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderInfo {

    @Id
    @JsonIgnore
    @Column(name = "rider_no")
    private Integer riderNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    // 고객명
    @Column(name = "name",  length =  45)
    private String name;

    @Column(name = "thumbnail", length = 512)
    private String thumbnail;

}
