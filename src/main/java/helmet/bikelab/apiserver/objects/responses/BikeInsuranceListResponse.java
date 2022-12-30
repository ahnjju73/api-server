package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.BikeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeInsuranceListResponse extends OriginObject {
    public BikeInsuranceListResponse(){}
    public BikeInsuranceListResponse(List<BikeInsurances> insurances, Integer bikeInsuranceNo){
        this.insurances = insurances;
        this.bikeInsuranceNo = bikeInsuranceNo;
    }
    private List insurances;
    private Integer bikeInsuranceNo;
    private String insuranceId;
    private Boolean isTransferred = false;
}
