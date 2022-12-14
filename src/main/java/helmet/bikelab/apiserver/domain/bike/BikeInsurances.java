package helmet.bikelab.apiserver.domain.bike;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.types.BikeInsuranceTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.SelfCoverCarTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeInsuranceTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.SelfCoverCarTypesConverter;
import helmet.bikelab.apiserver.objects.requests.BikeInsuranceInfo;
import helmet.bikelab.apiserver.objects.requests.UploadBikeInfo;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 비고 :
 * 대인2 가 0원이면 책임보험, 무한이면 종합보험.
 */
@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "bike_insurances")
public class BikeInsurances extends OriginObject {

    public BikeInsurances(){}

    public BikeInsurances(UploadBikeInfo bikeInsuranceInfo, Bikes bike, String insuranceId){
        this.insuranceId = insuranceId;
        this.stockNumber = bikeInsuranceInfo.getStockNumber();
        this.bikeNo = bike.getBikeNo();
        this.type = bikeInsuranceInfo.getType();
        this.bikeInsuranceType = bikeInsuranceInfo.getBikeInsuranceType();
        this.age = bikeInsuranceInfo.getAge();
        this.companyName = bikeInsuranceInfo.getCompanyName();
        this.liabilityMan = bikeInsuranceInfo.getLiabilityMan();
        this.liabilityCar = bikeInsuranceInfo.getLiabilityCar();
        this.liabilityMan2 = bikeInsuranceInfo.getLiabilityMan2();
        this.selfCoverCar = bikeInsuranceInfo.getSelfCoverCar();
        this.selfCoverMan = bikeInsuranceInfo.getSelfCoverMan();
        this.noInsuranceCover = bikeInsuranceInfo.getNoInsuranceCover();
        this.startAt = bikeInsuranceInfo.getStartAt();
        this.endAt = bikeInsuranceInfo.getEndAt();
        this.fee = bikeInsuranceInfo.getFee();
        this.grade = bikeInsuranceInfo.getGrade();
    }

    public BikeInsurances(BikeInsuranceInfo bikeInsuranceInfo, Bikes bike, String insuranceId){
        this.insuranceId = insuranceId;
        this.bikeNo = bike.getBikeNo();
        this.stockNumber = bikeInsuranceInfo.getStockNumber();
        this.type = bikeInsuranceInfo.getType();
        this.bikeInsuranceType = bikeInsuranceInfo.getBikeInsuranceType();
        this.age = bikeInsuranceInfo.getAge();
        this.companyName = bikeInsuranceInfo.getCompanyName();
        this.liabilityMan = bikeInsuranceInfo.getLiabilityMan();
        this.liabilityCar = bikeInsuranceInfo.getLiabilityCar();
        this.liabilityMan2 = bikeInsuranceInfo.getLiabilityMan2();
        this.selfCoverCar = bikeInsuranceInfo.getSelfCoverCar();
        this.selfCoverMan = bikeInsuranceInfo.getSelfCoverMan();
        this.noInsuranceCover = bikeInsuranceInfo.getNoInsuranceCover();
        this.startAt = bikeInsuranceInfo.getStartAt();
        this.endAt = bikeInsuranceInfo.getEndAt();
        this.fee = bikeInsuranceInfo.getFee();
        this.grade = bikeInsuranceInfo.getGrade();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "insurance_no", nullable = false)
    private Integer insuranceNo;

    @Column(name = "insurance_id", nullable = false)
    private String insuranceId;

    // 증권번호
    @Column(name = "stock_number", nullable = false)
    private String stockNumber;

    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String typeCode;

    @Column(name = "type", columnDefinition = "ENUM")
    @Convert(converter = InsuranceTypesConverter.class)
    private InsuranceTypes type;

    @Column(name = "insurance_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String bikeInsuranceTypeCode;

    @Column(name = "insurance_type", columnDefinition = "ENUM")
    @Convert(converter = BikeInsuranceTypesConverter.class)
    private BikeInsuranceTypes bikeInsuranceType = BikeInsuranceTypes.PAID;

    @Column(name = "insurance_age", columnDefinition = "TINYINT")
    private Integer age;

    @Column(name = "company_name", length = 45)
    private String companyName;

    @Column(name = "liability_man", nullable = false)
    private Integer liabilityMan;

    @Column(name = "liability_car", nullable = false)
    private Integer liabilityCar;

    // -1일 경우, '무한'.
    @Column(name = "liability_man2", nullable = false)
    private Integer liabilityMan2;

    @Column(name = "self_cover_man", nullable = false)
    private Integer selfCoverMan;

    @Column(name = "self_cover_car", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String selfCoverCarCode;

    @Column(name = "self_cover_car", columnDefinition = "ENUM")
    @Convert(converter = SelfCoverCarTypesConverter.class)
    private SelfCoverCarTypes selfCoverCar;

    @Column(name = "no_insurance_cover", nullable = false)
    private Integer noInsuranceCover;

    @Column(name = "start_at", columnDefinition = "DATETIME")
    private LocalDateTime startAt;

    @Column(name = "end_at", columnDefinition = "DATETIME")
    private LocalDateTime endAt;

    @Column(name = "grade")
    private String grade;

    @Column(name = "is_transferred", columnDefinition = "TINYINT(1)")
    private Boolean isTransferred = false;

    @Column(name = "fee")
    private Integer fee;

    @Column(name = "paid_fee")
    private Integer paidFee;

    @Column(name = "created_no", length = 21)
    private Integer createdUserNo;

    @ManyToOne
    @JoinColumn(name = "created_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    private BikeUser createdUser;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "updated_no", length = 21)
    private Integer updatedUserNo;

    @ManyToOne
    @JoinColumn(name = "updated_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    private BikeUser updatedUser;

    @Column(name = "paid_user_no", length = 21)
    private Integer paidUserNo;

    @ManyToOne
    @JoinColumn(name = "paid_user_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    private BikeUser paidUser;

    @Column(name = "paid_at", columnDefinition = "DATETIME")
    private LocalDateTime paidAt;

    public void setType (String type){
        this.type = InsuranceTypes.getInsuranceType(type);
    }

    public void setType (InsuranceTypes type){
        this.type = type;
    }

    public void checkValidation(){
        if(!bePresent(bikeInsuranceType)) withException("800-008");
        if(!bePresent(type)) withException("800-009");
        if(!bePresent(companyName)) withException("800-011");
        if(!bePresent(age)) withException("800-010");
        if(!bePresent(liabilityMan)) withException("800-001");
        if(!bePresent(liabilityCar)) withException("800-002");
        if(!bePresent(liabilityMan2)) withException("800-006");
        if(!bePresent(noInsuranceCover)) withException("800-005");
        if(!bePresent(selfCoverMan)) withException("800-004");
        if(!bePresent(selfCoverCar)) withException("800-003");
    }

    public void updateBikeInsuranceInfo(BikeInsuranceInfo bikeInsuranceInfo, BikeUser sessionUser){
        this.type = bikeInsuranceInfo.getType();
        this.bikeInsuranceType = bikeInsuranceInfo.getBikeInsuranceType();
        this.age = bikeInsuranceInfo.getAge();
        this.stockNumber = bikeInsuranceInfo.getStockNumber();
        this.companyName = bikeInsuranceInfo.getCompanyName();
        this.liabilityMan = bikeInsuranceInfo.getLiabilityMan();
        this.liabilityCar = bikeInsuranceInfo.getLiabilityCar();
        this.liabilityMan2 = bikeInsuranceInfo.getLiabilityMan2();
        this.selfCoverCar = bikeInsuranceInfo.getSelfCoverCar();
        this.selfCoverMan = bikeInsuranceInfo.getSelfCoverMan();
        this.noInsuranceCover = bikeInsuranceInfo.getNoInsuranceCover();
        this.startAt = bikeInsuranceInfo.getStartAt();
        this.endAt = bikeInsuranceInfo.getEndAt();
        this.fee = bikeInsuranceInfo.getFee();
        this.updatedUserNo = sessionUser.getUserNo();
        this.updatedAt = LocalDateTime.now();
        this.grade = bikeInsuranceInfo.getGrade();
    }

    public void setCreatedUser(BikeUser createdUser) {
        this.createdUser = createdUser;
        this.createdUserNo = createdUser.getUserNo();
        this.updatedUserNo = createdUser.getUserNo();
        this.updatedUser = createdUser;
    }

    public void setPaidFee(Integer paidFee, BikeUser user) {
        this.paidFee = paidFee;
        this.paidUser = user;
        this.paidUserNo = user.getUserNo();
        this.paidAt = LocalDateTime.now();
    }
}
