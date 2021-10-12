package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderBikeApproveRequest extends OriginObject {

    private String bikeId;
    private String riderId;

    public void checkValidation(){
        if(!bePresent(this.bikeId)) withException("");
        if(!bePresent(this.riderId)) withException("");
    }

}
