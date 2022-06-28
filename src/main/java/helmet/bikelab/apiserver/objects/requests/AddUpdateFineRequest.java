package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateFineRequest extends OriginObject {

    private Integer fee;
    private Integer paidFee;
    private String fineNum;
    private String fineType;
    private String fineDate;
    private String fineExpireDate;
    private String riderId;
    private String clientId;
    private String bikeId;

    public void checkValidation(){
        if(!bePresent(bikeId))
            withException("710-001");
        if(!bePresent(fineNum))
            withException("710-002");
        if(!bePresent(fineType))
            withException("710-006");
        if(!bePresent(fee))
            withException("710-003");
        if(!bePresent(fineDate))
            withException("710-004");
        if(!bePresent(fineExpireDate))
            withException("710-005");
    }
}
