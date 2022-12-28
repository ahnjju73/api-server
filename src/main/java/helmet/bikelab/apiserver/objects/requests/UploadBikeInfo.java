package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadBikeInfo extends OriginObject {

    private String vimNum;
    private String number;
    private String carModel;
    private BikeStatusTypes status;
    private String color;
    private Integer odometerByAdmin;

    private PayerTypes payerType = PayerTypes.COMPANY;

    private String warehouse;
    // 모든 비고정보를 취합한다.
    private String description;

    public void setPayerType(String payerType) {
        this.payerType = PayerTypes.getPayerTypes(payerType);
    }

    public void checkValidation(StringBuilder errorText){
        if(!bePresent(vimNum)) errorText.append("차대번호를 입력해주세요.\n");
        if(!bePresent(carModel)) errorText.append("차종(코드)을 선택해주세요.\n");
        if(!bePresent(status)) errorText.append("보관상태(코드)를 선택해주세요\n");
        if(!bePresent(color)) errorText.append("차량색상을 입력해주세요.\n");
    }

    public void setStatus(String status) {
        this.status = BikeStatusTypes.getBikeStatusTypes(status);
    }

}
