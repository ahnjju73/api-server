package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateBikeRequest extends OriginObject {
    private String bikeId;
    private String vimNumber;
    private String number;
    private String carModel;
    private String color;
    private Integer years;
    private String regNum;
    private String companyName;
    private Integer price;
    private LocalDateTime receiveDt;
    private LocalDateTime registerDt;

    public void setReceiveDt(String receiveDt) {
        try {
            this.receiveDt = LocalDateTime.parse(receiveDt + "T00:00:00");
        }catch (Exception e){
            this.receiveDt = LocalDateTime.parse(receiveDt);
        }
    }

    public void setRegisterDt(String registerDt) {
        if(bePresent(registerDt)) this.registerDt = LocalDateTime.parse(registerDt);
    }

    public void checkValidation(){
        if(!bePresent(this.vimNumber)) withException("500-002");
        if(!bePresent(this.carModel)) withException("500-004");
        if(!bePresent(this.color)) withException("500-006");
        if(!bePresent(this.receiveDt)) withException("500-007");
        if(!bePresent(this.years)) withException("500-008");
    }

}

