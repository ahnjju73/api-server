package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DiagramInfoRequest extends OriginObject {
    private String carModel;
    private String name;
    private List<PresignedURLVo> images;

    public void checkValidation(){
        if(!bePresent(name)) withException("550-002");
        if(!bePresent(carModel)) withException("550-003");
    }
}
