package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.BankInfoDto;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateRiderInsuranceRequest {
    private String insCompany;
    private Integer age;
    private String insRange;
    private RiderInfoDto riderInfoDto;
    private BankInfoDto bankInfoDto;

    private String bikeId;


    private Integer liabilityMan;
    private Integer liabilityCar;
    private Integer liabilityMan2;
    private Integer selfCoverMan;
    private Integer selfCoverCar;
    private Integer noInsuranceCover;
}
