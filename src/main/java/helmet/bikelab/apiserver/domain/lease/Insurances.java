package helmet.bikelab.apiserver.domain.lease;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceTypesConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "insurances")
public class Insurances extends OriginObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column(name = "insurance_no", nullable = false)
    private Integer insuranceNo;

    @Column(name = "insurance_id", nullable = false)
    private String insuranceId;

    @Column(name = "insurance_name", length = 40)
    private String insuranceName;

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

    public void setType (String type){
        this.type = InsuranceTypes.getInsuranceType(type);
    }
    public void setType (InsuranceTypes type){
        this.type = type;
    }

    public void checkValidation(){
        if(!bePresent(insuranceName)) withException("800-007");
        if(!bePresent(insuranceTypeCode)) withException("800-008");
        if(!bePresent(type)) withException("800-009");
        if(!bePresent(companyName)) withException("800-011");
        if(!bePresent(age)) withException("800-010");
        if(!bePresent(liabilityMan)) withException("800-001");
        if(!bePresent(liabilityCar)) withException("800-002");
        if(!bePresent(liabilityMan2)) withException("800-006");
        if(!bePresent(noInsuranceCover)) withException("800-005");
        if(!bePresent(selfCoverMan)) withException("800-004");
        if(!bePresent(selfCoverCar)) withException("800-003");
    }
}
