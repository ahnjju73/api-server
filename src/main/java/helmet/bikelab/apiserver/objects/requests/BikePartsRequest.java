package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikePartsRequest extends RequestListDto {
    private String carModel;
    private String partsId;
    private String merchantId;
    private String partsName;
    private String partsNameEng;
    private String partsTypeNo;
    private Integer partsPrice;
    private Double workingHour;

    public void checkValidation(){
        if(!bePresent(carModel)) writeMessage("차량모델을 선택해주세요.");
        if(!bePresent(partsId)) writeMessage("부품 ID를 선택해주세요.");
        if(!bePresent(merchantId)) writeMessage("제조사코드를 입력해주세요.");
        if(!bePresent(partsNameEng)) writeMessage("부품(영문)명을 입력해주세요.");
        if(!bePresent(partsPrice)) writeMessage("부품가격을 입력해주세요.");
        if(!bePresent(workingHour)) writeMessage("공임시간을 입력해주세요.");
    }
}
