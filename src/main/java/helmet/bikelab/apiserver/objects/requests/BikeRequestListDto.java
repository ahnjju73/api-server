package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeRequestListDto extends RequestListDto{

    private String searchClientId;
    private String searchName;
    private String searchBikeId;
    private String searchNumber;
    private String searchVim;
    private String isBikemaster;
    private String type;
    private String carModel;
    private String payerType;
    private String searchColor;
    private String searchWarehouse;
    private String searchBikeStatus;
}

