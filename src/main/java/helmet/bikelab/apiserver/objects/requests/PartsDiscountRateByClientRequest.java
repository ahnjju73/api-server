package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsDiscountRateByClientRequest extends OriginObject {
    private String clientId;
    private Double discountRate;
    private List<DiscountedParts> discountedParts = new ArrayList<>();

    public void checkValidation(){
        if(bePresent(discountedParts)){
            discountedParts.forEach(e -> {
                if(!bePresent(e.getPartsTypeNo())) withException("400-200");
            });
        }
    }
}
