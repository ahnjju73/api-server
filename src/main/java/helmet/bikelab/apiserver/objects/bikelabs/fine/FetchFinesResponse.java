package helmet.bikelab.apiserver.objects.bikelabs.fine;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.requests.StopLeaseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchFinesResponse {
    private String fineId;
    private LocalDateTime fineDate;
    private LocalDateTime fineExpireDate;
    private Integer fee;
    private StopLeaseDto stopInfo;
    private Integer paidFee;
    private String fineNum;
    private String leaseId;
    private BikeDto bike;
}
