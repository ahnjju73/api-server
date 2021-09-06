package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsUpdatedRequest extends OriginObject {

    private String partsId;
    private Long partsNo;
    private Integer partsPrices;
    private Integer workingPrices;
    private Double workingHours;
    private UnitTypes units;

    public void checkValidation(){
        if(!bePresent(this.partsId)) withException("503-008");
        if(!bePresent(this.partsPrices)) withException("503-003");
        if(!bePresent(this.workingPrices)) withException("503-004");
        if(!bePresent(this.workingHours)) withException("503-005");
        if(!bePresent(this.units)) withException("503-006");
    }

    public void setUnits(String units) {
        this.units = UnitTypes.getUnitTypes(units);
    }
}
