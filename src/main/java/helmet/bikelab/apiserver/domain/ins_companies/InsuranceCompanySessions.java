package helmet.bikelab.apiserver.domain.ins_companies;

import helmet.bikelab.apiserver.domain.types.InsuranceCompanySessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "insurance_company_sessions", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@IdClass(InsuranceCompanySessionPK.class)
@NoArgsConstructor
public class InsuranceCompanySessions {

    @Id
    @Column(name = "company_no")
    private Integer companyNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

    @ManyToOne
    @JoinColumn(name = "company_no", insertable = false, updatable = false)
    private InsuranceCompanies company;

    @Column(name = "session_key", length = 256)
    private String sessionKey;

    @Column(name = "salt", length = 256)
    private String salt;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}


