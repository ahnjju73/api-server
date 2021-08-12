package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeModelDto extends OriginObject {

    private String code;
    private String model;
    private String bikeTypeCode;
    private BikeTypes bikeType;
    private Integer manufacturerNo;
    private Double volume;
    private String rmk;

    @JsonIgnore
    private Manufacturers manufacturers;

    public void setBikeTypeCode(String bikeTypeCode) {
        this.bikeTypeCode = bikeTypeCode;
        this.bikeType = BikeTypes.getType(bikeTypeCode);
    }

    public void checkValidation(){
        if(!bePresent(this.model)) withException("502-001");
        if(!bePresent(this.bikeType)) withException("502-002");
        if(!bePresent(this.manufacturerNo)) withException("502-003");
        if(!bePresent(this.volume)) withException("502-004");
    }

}
