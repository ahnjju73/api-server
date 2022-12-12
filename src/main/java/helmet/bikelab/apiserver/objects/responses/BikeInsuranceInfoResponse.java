package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.BikeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.types.BikeInsuranceTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.SelfCoverCarTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeInsuranceInfoResponse extends OriginObject {
    public BikeInsuranceInfoResponse(){}

    public BikeInsuranceInfoResponse(BikeInsurances bikeInsurance){
        Bikes bike = bikeInsurance.getBike();
        this.insuranceId = bikeInsurance.getInsuranceId();
        this.stockNumber = bikeInsurance.getStockNumber();
        this.type = bikeInsurance.getTypeCode();
        this.setBikeInsuranceType(bikeInsurance.getBikeInsuranceTypeCode());
        this.age = bikeInsurance.getAge();
        this.companyName = bikeInsurance.getCompanyName();
        this.liabilityMan = bikeInsurance.getLiabilityMan();
        this.liabilityCar = bikeInsurance.getLiabilityCar();
        this.liabilityMan2 = bikeInsurance.getLiabilityMan2();
        this.selfCoverCar = bikeInsurance.getSelfCoverCarCode();
        this.selfCoverMan = bikeInsurance.getSelfCoverMan();
        this.noInsuranceCover = bikeInsurance.getNoInsuranceCover();
        if(bePresent(bikeInsurance.getStartAt())) this.setStartAt(bikeInsurance.getStartAt().toLocalDate().toString());
        if(bePresent(bikeInsurance.getEndAt())) this.setEndAt(bikeInsurance.getEndAt().toLocalDate().toString());
        this.fee = bikeInsurance.getFee();
        this.insuranceNo = bikeInsurance.getInsuranceNo();
        this.grade = bikeInsurance.getGrade();
        this.used = bePresent(bike.getBikeInsurance()) && bike.getBikeInsuranceNo().equals(bikeInsurance.getInsuranceNo()) ? true : false;
    }

    private String insuranceId;
    private String stockNumber;
    private String type;
    private String bikeInsuranceType;
    private Integer age;
    private String companyName;
    private Integer liabilityMan;
    private Integer liabilityCar;
    private Integer liabilityMan2;
    private Integer selfCoverMan;
    private String selfCoverCar;
    private Integer noInsuranceCover;

    private String startAt;

    private String endAt;

    private Integer fee;

    private Integer insuranceNo;
    private String grade;
    private Boolean used = false;
}
