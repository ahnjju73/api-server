package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateFineRequest extends OriginObject {

    private Integer fee;
    private Integer paidFee;
    private String fineNum;
    private LocalDateTime fineDate;
    private LocalDateTime fineExpireDate;
    private String riderId;
    private String clientId;
    private String bikeId;

    public void checkValidation(){
        if(!bePresent(bikeId))
            withException("");
        if(!bePresent(fineNum))
            withException("");
        if(!bePresent(fee))
            withException("");
        if(!bePresent(fineDate))
            withException("");
        if(!bePresent(fineExpireDate))
            withException("");

    }
}
