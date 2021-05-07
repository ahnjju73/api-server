package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateExtraRequest {
    private String extraId;
    private String leaseId;
    private String paymentId;
    private String extraType;
    private Integer paidFee;
    private String description;
    private Integer extraFee;
}
