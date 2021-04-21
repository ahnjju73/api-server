package helmet.bikelab.apiserver.objects.bikelabs.insurance;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.lease.Insurances;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InsuranceDto {
    private String companyName;
    private Integer insuranceAge;
    private Integer bmCare;
    private Integer liabilityMan;
    private Integer liabilityMan2;
    private Integer liabilityCar;
    private Integer selfCoverMan;
    private Integer selfCoverCar;
    private Integer noInsuranceCover;

    public void setInsurance(Insurances insurance){
        companyName = insurance.getCompanyName();
        insuranceAge = insurance.getAge();
        bmCare = insurance.getBmCare();
        liabilityMan = insurance.getLiabilityMan();
        liabilityMan2 = insurance.getLiabilityMan2();
        liabilityCar = insurance.getLiabilityCar();
        selfCoverCar = insurance.getSelfCoverCar();
        selfCoverMan = insurance.getSelfCoverMan();
        noInsuranceCover = insurance.getNoInsuranceCover();

    }
}
