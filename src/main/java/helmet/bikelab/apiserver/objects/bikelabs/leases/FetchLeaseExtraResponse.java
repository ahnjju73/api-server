package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchLeaseExtraResponse {
    //회차, 납부일자
    private String extraId;
    private LeasePaymentDto payment;
    private String extraType;
    private Integer extraFee;
    private String description;
    private Integer paidFee;

}
