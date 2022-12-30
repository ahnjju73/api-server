package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceTypesConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Deprecated
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_insurances")
public class LeaseInsurances extends OriginObject {

    @Id
    @Column(name = "lease_no")
    private Integer leaseNo;

    @JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "lease_no", updatable = false, insertable = false)
    private Leases lease;

    @Column(name = "insurance_type", nullable = false)
    private String insuranceTypeCode;

    @ManyToOne
    @JoinColumn(name = "insurance_type", insertable = false, updatable = false)
    private CommonCodeInsurances insuranceType;

    @Column(name = "insurance_age", columnDefinition = "TINYINT")
    private Integer age;

    @Column(name = "company_name", length = 45)
    private String companyName;

    @Column(name = "bm_care", columnDefinition = "TINYINT")
    private Integer bmCare;

    @Column(name = "liability_man", nullable = false)
    private Integer liabilityMan;

    @Column(name = "liability_car", nullable = false)
    private Integer liabilityCar;

    @Column(name = "liability_man2", nullable = false)
    private Integer liabilityMan2;

    @Column(name = "self_cover_man", nullable = false)
    private Integer selfCoverMan;

    @Column(name = "self_cover_car", nullable = false)
    private Integer selfCoverCar;

    @Column(name = "no_insurance_cover", nullable = false)
    private Integer noInsuranceCover;

    @Column(name = "type", columnDefinition = "ENUM")
    @Convert(converter = InsuranceTypesConverter.class)
    private InsuranceTypes type = InsuranceTypes.PERSONAL;

    @Column(name = "insurance_name", length = 40)
    private String insuranceName;

    public void setInsurance(Insurances insurance){
        this.insuranceTypeCode = insurance.getInsuranceTypeCode();
        this.age = insurance.getAge();
        this.companyName = insurance.getCompanyName();
        this.bmCare = insurance.getBmCare();
        this.liabilityMan = insurance.getLiabilityMan();
        this.liabilityCar = insurance.getLiabilityCar();
        this.liabilityMan2 = insurance.getLiabilityMan2();
        this.selfCoverMan = insurance.getSelfCoverMan();
        this.selfCoverCar = insurance.getSelfCoverCar();
        this.noInsuranceCover = insurance.getNoInsuranceCover();
        this.type = insurance.getType();
    }

}
