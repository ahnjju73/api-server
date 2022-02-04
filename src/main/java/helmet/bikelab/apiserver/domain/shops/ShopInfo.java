package helmet.bikelab.apiserver.domain.shops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.Banks;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "shop_info", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ShopInfo {

    @Id
    @JsonIgnore
    @Column(name = "shop_no")
    private Integer shopNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    // 고객명
    @Column(name = "name",  length =  45)
    private String name;

    @Column(name = "thumbnail", length = 512)
    private String thumbnail;

    @Column(name = "phone", length = 45)
    private String phone;

    @Column(name = "manager_name", length = 45)
    private String managerName;

    @Column(name = "start_time", columnDefinition = "TIME")
    private LocalTime startTime;

    @Column(name = "end_time", columnDefinition = "TIME")
    private LocalTime endTime;

    @Column(name = "bank_cd", length = 4)
    private String bankCd;

    @OneToOne
    @JoinColumn(name = "bank_cd", updatable = false, insertable = false)
    private Banks banks;

    @Column(name = "account", length = 45)
    private String account;

    @Column(name = "depositor", length = 45)
    private String depositor;

}
