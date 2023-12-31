package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StopLeaseDto {
    private String leaseStopStatus;
    private String leaseId;
    private String stopDt;
    private String stopReason;
    private Long stopPaidFee;
    private Long stopFee;
}
