package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadBikeInsurance extends BikeInsuranceInfo {

    private Boolean paid = false;

    public void checkValidation(StringBuilder errorText){
//        if(!bePresent(getGrade())) errorText.append("등급을 선택해주세요.\n");
//        if(!bePresent(getStockNumber())) errorText.append("증권번호를 입력해주세요.\n");
//        if(!bePresent(getFee())) errorText.append("보험료를 입력해주세요.\n");
//        if(!bePresent(getBikeInsuranceType())) errorText.append("보험용도를 입력해주세요.\n");
//        if(!bePresent(getType())) errorText.append("보험형태를 입력해주세요.\n");
//        if(!bePresent(getCompanyName())) errorText.append("보험사를 입력해주세요.\n");
//        if(!bePresent(getAge())) errorText.append("나이를 입력해주세요.\n");
//        if(!bePresent(getLiabilityMan())) errorText.append("대인금액을 입력해주세요.\n");
//        if(!bePresent(getLiabilityCar())) errorText.append("대물금액을 입력해주세요.\n");
//        if(!bePresent(getLiabilityMan2())) errorText.append("대인2금액을 입력해주세요.\n");
//        if(!bePresent(getSelfCoverMan())) errorText.append("자손금액을 입력해주세요.\n");
//        if(!bePresent(getSelfCoverCar())) errorText.append("자차금액을 입력해주세요.\n");
//        if(!bePresent(getNoInsuranceCover())) errorText.append("무보험상해 금액을 입력해주세요.\n");
    }

    public Boolean isAddableBikeInsurance(){
        return bePresent(this.getStartAt()) && bePresent(this.getEndAt());
    }

    // Setter

    public void setStartAt(String startAt) {
        try{
            if(bePresent(startAt)) this.setStartAt(LocalDateTime.parse(startAt));
        }catch (Exception e){

        }
    }

    public void setEndAt(String endAt) {
        try{
            if(bePresent(endAt)) this.setEndAt(LocalDateTime.parse(endAt));
        }catch (Exception e){

        }

    }

    public void setInsuranceType(String bikeInsuranceType) {
        super.setBikeInsuranceType(bikeInsuranceType);
    }

    public void setType(String type) {
        super.setType(type);
    }

    public void setSelfCoverCar(String selfCoverCar) {
        super.setSelfCoverCar(selfCoverCar);
    }

}
