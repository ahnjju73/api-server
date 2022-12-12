package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.PayerTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddBikeRequest extends OriginObject {
    private String vimNumber;
    private String number;
    private String carModel;
    private String color;
    private Integer years;
    private LocalDateTime receiveDt;
    private LocalDateTime registerDt;
    private String regNum;
    private String companyName;
    private Integer price;
    private String description;
    private Boolean isBikemaster;
    private Boolean isMt;
    private String payerTypeCode;
    private PayerTypes payerType;

    public void setPayerTypeCode(String payerTypeCode) {
        this.payerTypeCode = payerTypeCode;
        this.payerType = PayerTypes.getPayerTypes(payerTypeCode);
    }

    public void setReceiveDt(String receiveDt) {
        this.receiveDt = LocalDateTime.parse(receiveDt);
    }
    public void setRegisterDt(String registerDt) {
        this.registerDt = LocalDateTime.parse(registerDt);
    }

    public void checkValidation(){
        if(!bePresent(this.vimNumber)) withException("500-002");
        if(!bePresent(this.carModel)) withException("500-004");
        if(!bePresent(this.color)) withException("500-006");
        if(!bePresent(this.receiveDt)) withException("500-007");
    }
}
