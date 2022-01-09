package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
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
    private Double workingHours;
    private YesNoTypes isFreeSupport;

    public void checkValidation(){
        if(!bePresent(this.partsId)) withException("503-008");
        if(!bePresent(this.partsPrices)) withException("503-003");
        if(!bePresent(this.workingHours)) withException("503-005");
    }

    public void setIsFreeSupport(String isFreeSupport) {
        this.isFreeSupport = YesNoTypes.getYesNo(isFreeSupport);
    }
}
