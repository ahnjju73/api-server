package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DiagramInfoRequest extends OriginObject {
    private String carModel;
    private String name;

    public void checkValidation(){
        if(!bePresent(carModel)) withException("");
        if(!bePresent(name)) withException("");
    }
}
