package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchBikeModelsResponse {
    private String code;
    private BikeTypes bikeType;
    private String make;
    private String model;
    private Double volume;
    private Boolean discontinue;
}
