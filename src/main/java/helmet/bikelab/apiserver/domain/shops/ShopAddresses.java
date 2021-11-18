package helmet.bikelab.apiserver.domain.shops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "shop_addresses", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShopAddresses {

    @Id
    @Column(name = "shop_no")
    private Integer shopNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Embedded
    private ModelAddress modelAddress = new ModelAddress();

    @Column(name = "lng")
    private Double longitude;
    @Column(name = "lat")
    private Double latitude;

}
