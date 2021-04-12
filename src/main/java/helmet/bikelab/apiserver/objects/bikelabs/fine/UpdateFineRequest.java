package helmet.bikelab.apiserver.objects.bikelabs.fine;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateFineRequest extends OriginObject {
    private String fineId;
    private String paymentId;
    private String fineNum;
    private LocalDateTime fineDate;
    private LocalDateTime expireDate;
    private Integer fee;
    private Integer paidFee;

    public void setFineDate(String fineDate){
        this.fineDate =  LocalDateTime.parse(fineDate);
    }
    public void setExpireDate(String expireDate){
        this.expireDate = LocalDateTime.parse(expireDate);
    }

    public void checkValidation(){
        if(!bePresent(fineId)) withException("");
    }

}
