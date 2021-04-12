package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddBikeRequest extends OriginObject {
    private String vimNumber;
    private String number;
    private String carModel;
    private String color;
    private LocalDateTime receiveDt;
    private LocalDateTime registerDt;

    public void setReceiveDt(String receiveDt) {
        this.receiveDt = LocalDateTime.parse(receiveDt);
    }
    public void setRegisterDt(String registerDt) {
        this.registerDt = LocalDateTime.parse(registerDt);
    }

    public void checkValidation(){
        if(!bePresent(this.vimNumber)) withException("500-002");
        if(!bePresent(this.number)) withException("500-003");
        if(!bePresent(this.carModel)) withException("500-004");
        if(!bePresent(this.color)) withException("500-005");

        //todo: 길이 체크
        //if(this.vimNumber.length() > 12) withException("");
    }
}
