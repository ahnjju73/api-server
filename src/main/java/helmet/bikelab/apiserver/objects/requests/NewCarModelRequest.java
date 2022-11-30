package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewCarModelRequest extends OriginObject {
    private Integer manufacturerNo;
    private String model;
    private Double volume;
    private BikeTypes bikeType = BikeTypes.GAS;
    private Boolean discontinue = false;
    private Integer year;

    public void checkValidation(){
        if(!bePresent(this.year)) withException("");
    }
    public void setBikeTypeCode(String bikeTypeCode) {
        this.bikeType = BikeTypes.getType(bikeTypeCode);
    }
}
