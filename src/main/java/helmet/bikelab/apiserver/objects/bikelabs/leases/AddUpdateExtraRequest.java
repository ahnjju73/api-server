package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateExtraRequest extends OriginObject {
    private String extraId;
    private String leaseId;
    private String paymentId;
    private String extraType;
    private Integer paidFee;
    private String description;
    private Integer extraFee;

    public void checkValidation(){
        if(!bePresent(extraType)) withException("860-004");
        if(!bePresent(extraFee)) withException("860-002");
        if(!bePresent(paidFee)) withException("860-003");


    }


}
