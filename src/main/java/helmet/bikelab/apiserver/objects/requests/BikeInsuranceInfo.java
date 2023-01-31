package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.types.BikeInsuranceTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.SelfCoverCarTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeInsuranceTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceTypesConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeInsuranceInfo extends OriginObject {
    private String insuranceId;
    private String stockNumber;
    private String bikeId;
    private InsuranceTypes type;
    private BikeInsuranceTypes bikeInsuranceType;
    private Integer age;
    private String companyName;
    private Integer liabilityMan;
    private Integer liabilityCar;
    private Integer liabilityMan2;
    private Integer selfCoverMan;
    private SelfCoverCarTypes selfCoverCar;
    private Integer noInsuranceCover;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    private Integer fee;

    private String grade;

    private String description;

    private Integer penalty;
    private Integer refund;
    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public void setStartAt(String startAt) {
        try {
            this.startAt = LocalDateTime.parse(startAt);
        }catch (Exception e){

        }
    }

    public void setEndAt(String endAt) {
        try {
            this.endAt = LocalDateTime.parse(endAt);
        }catch (Exception e){

        }

    }

    public void setType(String type) {
        this.type = InsuranceTypes.getInsuranceType(type);
    }

    public void setBikeInsuranceType(String bikeInsuranceType) {
        this.bikeInsuranceType = BikeInsuranceTypes.getBikeInsuranceTypes(bikeInsuranceType);
    }

    public void setSelfCoverCar(String selfCoverCar) {
        this.selfCoverCar = SelfCoverCarTypes.getSelfCoverCarTypes(selfCoverCar);
    }

    public void checkValidation(){
        if(!bePresent(this.stockNumber)) writeMessage("증권번호를 입력해주세요.");
        if(!bePresent(this.type)) writeMessage("보험형태를 선택하세요.");
        if(!bePresent(this.bikeInsuranceType)) writeMessage("보험용도를 선택하세요.");
        if(!bePresent(this.age)) writeMessage("나이 선택하세요.");
        if(!bePresent(this.companyName)) writeMessage("보험사를 선택하세요.");
        if(!bePresent(this.liabilityMan)) writeMessage("대인 선택하세요.");
        if(!bePresent(this.liabilityCar)) writeMessage("대물 선택하세요.");
        if(!bePresent(this.liabilityMan2)) writeMessage("대인2 선택하세요.");
        if(!bePresent(this.selfCoverMan)) writeMessage("무보험차 상해 선택하세요.");
        if(!bePresent(this.selfCoverCar)) writeMessage("자손을 선택하세요.");
        if(!bePresent(this.noInsuranceCover)) writeMessage("자차 선택하세요.");
        if(!bePresent(this.startAt)) writeMessage("시작일 선택하세요.");
        if(!bePresent(this.endAt)) writeMessage("종료일 선택하세요.");
        if(!bePresent(this.fee)) writeMessage("납부료 선택하세요.");
        if(!bePresent(this.grade)) writeMessage("등급을 선택하세요.");
    }
}
