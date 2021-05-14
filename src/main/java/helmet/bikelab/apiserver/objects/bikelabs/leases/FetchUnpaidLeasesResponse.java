package helmet.bikelab.apiserver.objects.bikelabs.leases;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchUnpaidLeasesResponse extends OriginObject {
    private String leaseId;
    private String clientId;
    private String bikeId;
    private ClientDto client;
    private BikeDto bike;
    private Integer unpaidLeaseFee;
    private Integer unpaidExtraFee;

}
