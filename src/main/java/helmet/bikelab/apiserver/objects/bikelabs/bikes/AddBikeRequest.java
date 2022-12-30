package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeStatusTypes;
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
    private String regNum;
    private String companyName;
    private Integer price;
    private String description;
    private Boolean isBikemaster;
    private Boolean isMt = false;
    private String payerTypeCode;
    private PayerTypes payerType;
    private String warehouse;
    private BikeStatusTypes bikeStatusType;

    public void setBikeStatusType(String bikeStatusType) {
        this.bikeStatusType = BikeStatusTypes.getBikeStatusTypes(bikeStatusType);
    }

    public void setPayerTypeCode(String payerTypeCode) {
        this.payerTypeCode = payerTypeCode;
        this.payerType = PayerTypes.getPayerTypes(payerTypeCode);
    }

    public void checkValidation(){
        if(!bePresent(this.vimNumber)) withException("500-002");
        if(!bePresent(this.carModel)) withException("500-004");
        if(!bePresent(this.color)) withException("500-006");
        if(!bePresent(this.warehouse)) writeMessage("보관지를 선택해주세요.");
        if(bePresent(bikeStatusType) && BikeStatusTypes.RIDING.equals(bikeStatusType)){
            writeMessage("운영중상태로 등록될수 없습니다.");
        }else if(bePresent(bikeStatusType) && BikeStatusTypes.PENDING.equals(bikeStatusType)){
            if(!bePresent(this.warehouse))
                writeMessage("보관지를 선택해주세요.");
        }else {
            this.warehouse = null;
        }
    }
}
