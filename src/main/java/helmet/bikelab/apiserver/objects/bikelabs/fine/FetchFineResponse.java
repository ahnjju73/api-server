package helmet.bikelab.apiserver.objects.bikelabs.fine;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchFineResponse extends OriginObject {
    private LocalDateTime fineDate;
    private LocalDateTime fineExpireDate;
    private String clientName;
    private String clientId;
    private Integer fee;
    private Integer paidFee;
    private String bikeId;
    private String bikeNum;
    private String fineId;
    private String fineNum;


}
