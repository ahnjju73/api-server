package helmet.bikelab.apiserver.objects.bikelabs.fine;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchFinesResponse {
    private String fineId;
    private LocalDateTime fineDate;
    private Integer fee;
    private Integer paidFee;
    private String bikeNum;
    private String fineNum;
    private String bikeId;
    private LocalDateTime fineExpireDate;

}
