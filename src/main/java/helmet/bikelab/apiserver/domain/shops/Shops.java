package helmet.bikelab.apiserver.domain.shops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.ShopStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.ShopStatusTypesConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "shops", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class Shops {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_no")
    private Integer shopNo;

    @Column(name = "shop_id", length = 21, unique = true, nullable = false)
    private String shopId;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = ShopStatusTypesConverter.class)
    private ShopStatusTypes status = ShopStatusTypes.PENDING;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String statusCode;

    @Column(name = "email")
    private String email;

    @Column(name = "reg_no", length = 45)
    private String regNum;

    @Column(name = "usable", columnDefinition = "TINYINT(1)")
    private Boolean usable = false;

    @Column(name = "created_at", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "shop", fetch = FetchType.EAGER)
    private ShopInfo shopInfo;

    @OneToOne(mappedBy = "shop", fetch = FetchType.EAGER)
    private ShopAddresses shopAddress;

    @OneToOne(mappedBy = "shop", fetch = FetchType.EAGER)
    private ShopPassword shopPassword;

}
