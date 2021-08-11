package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeModelDto {
    private String code;
    private String make;
    private String model;
    private BikeTypes bikeType;
    private Double volume;
    private Boolean discontinue;
}
