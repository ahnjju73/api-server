package helmet.bikelab.apiserver.domain.client;

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

@Entity
@Getter
@Setter
@Table(name = "clients", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class Clients {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_no")
    private  Integer clientNo;

    @Column(name = "client_id", length = 21)
    private String clientId;

    @Column(name = "group_no")
    private Integer groupId;

    @ManyToOne
    @JoinColumn(name = "group_no", insertable = false, updatable = false)
    ClientGroups clientGroup;

    @Column(name = "direct_yn", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes directType;

    @OneToOne(mappedBy = "client", optional = false)
    private ClientInfo clientInfo;

    @Column(name = "status", columnDefinition = "ENUM")
    @Convert(converter = AccountStatusConverter.class)
    private AccountStatusTypes status;

    @Column(name = "email")
    private String email;

    @Column(name = "uuid", length = 50)
    private String uuid;

    @Column(name = "reg_no", length = 50)
    private String regNum;

    @Column(name = "created_at")
    private LocalDateTime createdAt;




}
