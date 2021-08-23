package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DemandLeaseByIdRequest extends OriginObject {

    private String demandLeaseId;
    private String bikeId;
    private Integer leaseFee;
    private String insuranceId;
    private String start;
    private String contractDate;

    public void checkValidation(){
        if(!bePresent(bikeId)) withException("803-003");
        if(!bePresent(leaseFee)) withException("803-004");
        if(!bePresent(insuranceId)) withException("803-005");
        if(!bePresent(start)) withException("803-006");
        if(!bePresent(contractDate)) withException("803-007");
    }

}
