package helmet.bikelab.apiserver.domain.ins_companies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelInsuranceImage;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.domain.types.converters.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "insurance_companies")
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InsuranceCompanies {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_no")
    private Integer companyNo;

    @Column(name = "chasoo", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String chasoo;

    @Column(name = "chasoo", columnDefinition = "ENUM")
    @Convert(converter = ChasooConveter.class)
    private Chasoo chasooStatus = Chasoo.ALWAYS;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "status", columnDefinition = "ENUM")
    @Convert(converter = AccountStatusConverter.class)
    private AccountStatusTypes status = AccountStatusTypes.PENDING;

    @Column(name = "status", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String statusCode;

    @Column(name = "dept_nm")
    private String deptNm;

    @Column(name = "dept_center")
    private String deptCenter;

    @Column(name = "position", length = 45)
    private String position;

    @Column(name = "position_role", length = 45)
    private String positionRole;

    @Column(name = "company_name", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String companyName;

    @Column(name = "company_name", columnDefinition = "ENUM")
    @Convert(converter = InsCompanyTypeConverter.class)
    private InsCompanyTypes companyNameType = InsCompanyTypes.KB;

    @Column(name = "role_type", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String role;

    @Column(name = "role_type", columnDefinition = "ENUM")
    @Convert(converter = RoleTypeConverter.class)
    private RoleTypes roleType;

    @Column(name = "name", length = 21)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "logo", columnDefinition = "JSON")
    @Convert(converter = ModelInsuranceImageConverter.class)
    private List<ModelInsuranceImage> logoImageList;

    @JsonIgnore
    @Column(name = "logo", columnDefinition = "JSON", insertable = false, updatable = false)
    private String logoImages;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
