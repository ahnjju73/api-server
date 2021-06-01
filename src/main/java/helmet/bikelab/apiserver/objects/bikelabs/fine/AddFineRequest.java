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
    private LocalDateTime expireDate;
    private Integer fee;
    private Integer paidFee;
    private String bikeId;

    public void setFineDate(String fineDate){
        try {
            this.fineDate = LocalDateTime.parse(fineDate);
        }catch (Exception e){
            this.fineDate = LocalDateTime.parse(fineDate + "T00:00:00");
        }
    }
    public void setExpireDate(String expireDate){
        try {
            this.expireDate = LocalDateTime.parse(expireDate);
        }catch (Exception e){
            this.expireDate = LocalDateTime.parse(expireDate + "T00:00:00");
        }
    }

    public void checkValidation(){
        if(!bePresent(fineNum)) withException("700-002");
        if(!bePresent(fee)) withException("700-004");
        if(!bePresent(fineDate)) withException("700-006");
        if(!bePresent(expireDate)) withException("700-007");
    }
}
