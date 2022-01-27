package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikePartsDto extends RequestListDto {
    private String carModel;
    private String clientId;
    private Integer partsCodeNo;
    private Long partsNo;
    private Integer partsTypeNo;
    private String partsTypeName;
    private String partsName;
    private Integer partsPrice;
    private Integer workingPrices;
    private Double workingHours;
    private Double clientDiscountRate;
    private String isFreeSupport;
    private Integer number;

}
