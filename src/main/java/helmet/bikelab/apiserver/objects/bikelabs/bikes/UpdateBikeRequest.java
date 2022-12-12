package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeStatusTypes;
import helmet.bikelab.apiserver.domain.types.PayerTypes;
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
    private String description;

    private Boolean isBikemaster;
    private Boolean isMt;
    private String payerTypeCode;
    private PayerTypes payerType;
    private BikeStatusTypes bikeStatusType;
    private Integer odometerByAdmin = 0;

    public void setBikeStatusType(BikeStatusTypes bikeStatusType) {
        this.bikeStatusType = bikeStatusType;
    }

    public void setBikeStatusType(String bikeStatusType) {
        this.bikeStatusType = BikeStatusTypes.getBikeStatusTypes(bikeStatusType);
    }

    public void setPayerTypeCode(String payerTypeCode) {
        this.payerTypeCode = payerTypeCode;
        this.payerType = PayerTypes.getPayerTypes(payerTypeCode);
    }

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
//        if(!bePresent(this.color)) withException("500-006");
//        if(!bePresent(this.receiveDt)) withException("500-007");
//        if(!bePresent(this.years)) withException("500-008");
    }

}

