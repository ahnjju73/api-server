package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.objects.BankInfoDto;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateRiderInsuranceRequest extends OriginObject {
    private String insCompany;
    private String insNum;
    private String ssn;
    private ModelAddress address;
    private String age;
    private String insRange;
    private RiderInfoDto riderInfoDto;
    private BankInfoDto bankInfoDto;

    private String bikeNum;
    private String vimNum;
    private String bikeType;

    private String usage;
    private String additionalStandard;

    private Integer liabilityMan;
    private Integer liabilityCar;
    private Integer liabilityMan2;
    private Integer selfCoverMan;
    private Integer selfCoverCar;
    private Integer noInsuranceCover;

    public void checkValidation(){
        if(!bePresent(insCompany))
            withException("");
        if(!bePresent(insNum))
            withException("");
        if(!bePresent(age))
            withException("");
        if(!bePresent(insRange))
            withException("");
        if(!bePresent(vimNum))
            withException("");
        if(!bePresent(bikeType))
            withException("");
        if(!bePresent(usage))
            withException("");
        if(!bePresent(additionalStandard))
            withException("");
    }
}
