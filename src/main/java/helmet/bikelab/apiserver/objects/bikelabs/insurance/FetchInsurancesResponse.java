package helmet.bikelab.apiserver.objects.bikelabs.insurance;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.SecurityTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchInsurancesResponse extends OriginObject {
    private Integer insuranceAge;
    private String companyName;
    private Integer insuranceFee;
    private Integer bmCare;
    private Integer liabilityMan;
    private Integer liabilityCar;

}
