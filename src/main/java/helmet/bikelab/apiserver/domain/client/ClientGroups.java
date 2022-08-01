package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.converters.AccountStatusConverter;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceTypesConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "groups", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class ClientGroups {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_no", nullable = false)
    private Integer groupNo;

    @Column(name = "group_id", unique = true, nullable = false, length = 21)
    private String groupId;

    @Column(name = "group_name")
    private String groupName;

    @JsonIgnore
    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = AccountStatusConverter.class)
    private AccountStatusTypes status = AccountStatusTypes.PENDING;

    //대표
    @Column(name = "ceo_name")
    private String ceoName;

    @Column(name = "ceo_phone")
    private String ceoPhone;

    @Column(name = "ceo_email")
    private String ceoEmail;

    @Column(name = "reg_no")
    private String regNum;

    @Column(name = "email", nullable = false)
    private String groupEmail;

    @OneToOne(mappedBy = "group", fetch = FetchType.EAGER)
    private GroupAddresses groupAddresses;

    @JsonIgnore
    @OneToOne(mappedBy = "group", fetch = FetchType.EAGER)
    private GroupPasswords groupPasswords;

}
