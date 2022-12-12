package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeInsuranceTypes;
import helmet.bikelab.apiserver.domain.types.BikeStatusTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.SelfCoverCarTypes;
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
    private String stockNumber;
    private BikeStatusTypes status;
    private String color;
    private Integer odometerByAdmin;
    /* 구매처 정보 시작 */
    private String companyName;
    private String regNum;
    private Integer price;
    private LocalDateTime receiveDt;
    private String description;

    /* 보험정보 */
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer fee;
    private BikeInsuranceTypes bikeInsuranceType;
    private InsuranceTypes type;
    private String insuranceCompanyName;
    private Integer age;
    private Integer liabilityMan;
    private Integer liabilityCar;
    private Integer liabilityMan2;
    private Integer noInsuranceCover;
    private Integer selfCoverMan;
    private SelfCoverCarTypes selfCoverCar;
    private String grade;

    public void checkValidation(StringBuilder errorText){
        if(!bePresent(vimNum)) errorText.append("차대번호를 입력해주세요.\n");
        if(!bePresent(number)) errorText.append("차량번호를 입력해주세요\n");
        if(!bePresent(carModel)) errorText.append("차종(코드)을 선택해주세요.\n");
        if(!bePresent(status)) errorText.append("보관상태(코드)를 선택해주세요\n");
        if(!bePresent(color)) errorText.append("차량색상을 입력해주세요.\n");
    }

    public void checkValidationBikeInsurance(StringBuilder errorText){
        if(!bePresent(grade)) errorText.append("등급을 선택해주세요.\n");
        if(!bePresent(stockNumber)) errorText.append("증권번호를 입력해주세요.\n");
        if(!bePresent(fee)) errorText.append("보험료를 입력해주세요.\n");
        if(!bePresent(bikeInsuranceType)) errorText.append("보험용도를 입력해주세요.\n");
        if(!bePresent(type)) errorText.append("보험형태를 입력해주세요.\n");
        if(!bePresent(insuranceCompanyName)) errorText.append("보험사를 입력해주세요.\n");
        if(!bePresent(age)) errorText.append("나이를 입력해주세요.\n");
        if(!bePresent(liabilityMan)) errorText.append("대인금액을 입력해주세요.\n");
        if(!bePresent(liabilityCar)) errorText.append("대물금액을 입력해주세요.\n");
        if(!bePresent(liabilityMan2)) errorText.append("대인2금액을 입력해주세요.\n");
        if(!bePresent(selfCoverMan)) errorText.append("자손금액을 입력해주세요.\n");
        if(!bePresent(selfCoverCar)) errorText.append("자차금액을 입력해주세요.\n");
        if(!bePresent(noInsuranceCover)) errorText.append("무보험상해 금액을 입력해주세요.\n");
    }

    public Boolean isAddableBikeInsurance(){
        return bePresent(this.startAt) && bePresent(this.endAt);
    }

    // Setter

    public void setStartAt(String startAt) {
        try{
            if(bePresent(startAt)) this.startAt = LocalDateTime.parse(startAt);
        }catch (Exception e){

        }
    }

    public void setEndAt(String endAt) {
        try{
            if(bePresent(endAt)) this.endAt = LocalDateTime.parse(endAt);
        }catch (Exception e){

        }

    }

    public void setReceiveDt(String receiveDt) {
        try{
            if(bePresent(receiveDt)) this.receiveDt = LocalDateTime.parse(receiveDt);
        }catch (Exception e){

        }
    }

    public void setInsuranceType(String bikeInsuranceType) {
        this.bikeInsuranceType = BikeInsuranceTypes.getBikeInsuranceTypes(bikeInsuranceType);
    }

    public void setType(String type) {
        this.type = InsuranceTypes.getInsuranceType(type);
    }

    public void setStatus(String status) {
        this.status = BikeStatusTypes.getBikeStatusTypes(status);
    }

    public void setSelfCoverCar(String selfCoverCar) {
        this.selfCoverCar = SelfCoverCarTypes.getSelfCoverCarTypes(selfCoverCar);
    }

}
