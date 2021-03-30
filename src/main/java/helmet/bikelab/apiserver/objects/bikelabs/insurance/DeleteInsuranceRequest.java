package helmet.bikelab.apiserver.objects.bikelabs.insurance;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DeleteInsuranceRequest extends OriginObject {
    private String insuranceId;

    public void checkValidation(){
        if(!bePresent(insuranceId)) withException("800-001");
    }
}
