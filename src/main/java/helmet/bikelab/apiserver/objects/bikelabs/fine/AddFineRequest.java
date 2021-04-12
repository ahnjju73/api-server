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
public class AddFineRequest extends OriginObject {
    private String fineNum;
    private LocalDateTime fineDate;
    private Integer fee;
    private LocalDateTime expireDate;
    private String paymentId;

    public void setFineDate(String fineDate){
        this.fineDate = LocalDateTime.parse(fineDate);
    }
    public void setExpireDate(String expireDate){
        this.expireDate = LocalDateTime.parse(expireDate);
    }

    public void checkValidation(){
        if(!bePresent(fineNum)) withException("700-002");
        if(!bePresent(fee)) withException("700-004");
        if(!bePresent(fineDate)) withException("700-006");
        if(!bePresent(expireDate)) withException("700-007");
    }
}
