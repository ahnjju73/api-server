package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.types.SettleStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.SettleStatusTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "settles", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Settles {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settle_no")
    private Long settleNo;

    @Column(name = "settle_id", unique = true, nullable = false)
    private String settleId;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = SettleStatusTypeConverter.class)
    private SettleStatusTypes settleStatusType = SettleStatusTypes.SCHEDULE;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String settleStatusTypeCode;

    @Column(name = "shop_no", nullable = false)
    private Integer shopNo;

    @ManyToOne
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "bank_cd", length = 4)
    private String bankCd;

    @OneToOne
    @JoinColumn(name = "bank_cd", updatable = false, insertable = false)
    private Banks banks;


    @Column(name = "confirmed_user_no")
    private Integer confirmedUserNo;

    @ManyToOne
    @JoinColumn(name = "confirmed_user_no", updatable = false, insertable = false)
    private BikeUser confirmedUser;

    @Column(name = "account", length = 45)
    private String account;

    @Column(name = "depositor", length = 45)
    private String depositor;

}
