package helmet.bikelab.apiserver.domain.shops;

import helmet.bikelab.apiserver.domain.types.AccountTypes;
import helmet.bikelab.apiserver.domain.types.converters.AccountTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "shop_accounts")
public class ShopAccounts {

    @Id
    @Column(name = "shop_no")
    private Integer shopNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_no", insertable = false, updatable = false)
    private Shops shop;

    @Column(name = "account_type", length = 21, columnDefinition = "ENUM")
    @Convert(converter = AccountTypeConverter.class)
    private AccountTypes accountType;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "notification_token")
    private String notificationToken;

    @Column(name = "ref_key1")
    private String refKey1;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

}
