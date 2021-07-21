package helmet.bikelab.apiserver.domain.riders;

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
@Table(name = "rider_accounts")
public class RiderAccounts {

    @Id
    @Column(name = "rider_no")
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "account_type", length = 21, columnDefinition = "ENUM")
    @Convert(converter = AccountTypeConverter.class)
    private AccountTypes accountType;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "ref_key1")
    private String refKey1;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

}
