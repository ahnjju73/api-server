package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PayLeaseFeeRequest {
    private List<String> payments;
    private String paidType;
    private Integer adjustmentFee;
}
