package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.shops.Shops;
import helmet.bikelab.apiserver.domain.embeds.ModelBankAccount;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "settles", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Settles extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settle_no")
    private Long settleNo;

    @Column(name = "settle_id", length = 21, unique = true, nullable = false)
    private String settleId;

    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Column(name = "created_at", columnDefinition = "default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "confirmed_at", columnDefinition = "default CURRENT_TIMESTAMP")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmed_user_no", length = 21)
    private Integer confirmedUserNo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "confirmed_user_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    private BikeUser confirmedUser;

    @Embedded
    private ModelBankAccount bankAccount = new ModelBankAccount();

    @JsonIgnore
    @OneToMany(mappedBy = "settle", fetch = FetchType.LAZY)
    private List<Estimates> estimates;

}
