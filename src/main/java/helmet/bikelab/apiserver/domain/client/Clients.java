package helmet.bikelab.apiserver.domain.client;

import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.AccountStatusConverter;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
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
@Table(name = "clients", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_no")
    private Integer clientNo;

    @Column(name = "client_id", length = 21, unique = true, nullable = false)
    private String clientId;

    @Column(name = "group_no", nullable = false)
    private Integer groupNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "group_no", insertable = false, updatable = false)
    private ClientGroups clientGroup;

    @Column(name = "direct_yn", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes directType;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = AccountStatusConverter.class)
    private AccountStatusTypes status = AccountStatusTypes.PENDING;

    @Column(name = "email")
    private String email;

    @Column(name = "uuid", length = 100)
    private String uuid;
 
    @Column(name = "reg_no", length = 45)
    private String regNum;

    @Column(name = "created_at", columnDefinition = "CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "client", optional = false, fetch = FetchType.EAGER)
    private ClientInfo clientInfo = new ClientInfo();

    @OneToOne(mappedBy = "client", optional = false, fetch = FetchType.EAGER)
    private ClientAddresses clientAddresses = new ClientAddresses();

    @OneToOne(mappedBy = "client", optional = false, fetch = FetchType.EAGER)
    private ClientPassword clientPassword = new ClientPassword();

    @OneToMany(mappedBy = "clients", fetch = FetchType.LAZY)
    private List<Leases> lease;

}
