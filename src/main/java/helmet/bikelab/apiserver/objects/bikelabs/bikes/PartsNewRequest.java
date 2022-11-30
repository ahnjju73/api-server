package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsNewRequest extends OriginObject {

    private String partsId;
    private String merchantId;
    private String carModel;
    private Integer partsCodeNo;
    private Integer partsPrices;
    private Integer workingPrices;
    private Double workingHours;
    private UnitTypes units;

    public void checkValidation(){
        if(!bePresent(this.partsId)) withException("503-008");
        if(!bePresent(this.carModel)) withException("500-004");
        if(!bePresent(this.partsCodeNo)) withException("");
        if(!bePresent(this.partsPrices)) withException("");
        if(!bePresent(this.workingPrices)) withException("");
        if(!bePresent(this.workingHours)) withException("");
        if(!bePresent(this.units)) withException("");
    }

    public void setUnits(String units) {
        this.units = UnitTypes.getUnitTypes(units);
    }
}
