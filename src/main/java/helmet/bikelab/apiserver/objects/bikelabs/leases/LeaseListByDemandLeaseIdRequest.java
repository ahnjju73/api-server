package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeaseListByDemandLeaseIdRequest extends PageableRequest {
    private String demandLeaseId;
}
