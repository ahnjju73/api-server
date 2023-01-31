package helmet.bikelab.apiserver.domain.bike;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.types.BikeInsuranceTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;
import helmet.bikelab.apiserver.domain.types.SelfCoverCarTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeInsuranceTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.InsuranceTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.SelfCoverCarTypesConverter;
import helmet.bikelab.apiserver.objects.requests.BikeInsuranceInfo;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "bike_insurance_histories")
public class BikeInsuranceHistories extends OriginObject {

    public BikeInsuranceHistories(){}

    public BikeInsuranceHistories(BikeInsurances bi){
        this.insuranceNo = bi.getInsuranceNo();
        this.insuranceId = bi.getInsuranceId();
        this.stockNumber = bi.getStockNumber();
        this.bikeNo = bi.getBikeNo();
        this.type = bi.getType();
        this.bikeInsuranceType = bi.getBikeInsuranceType();
        this.age = bi.getAge();
        this.companyName = bi.getCompanyName();
        this.liabilityMan = bi.getLiabilityMan();
        this.liabilityCar = bi.getLiabilityCar();
        this.liabilityMan2 = bi.getLiabilityMan2();
        this.selfCoverMan = bi.getSelfCoverMan();
        this.selfCoverCar = bi.getSelfCoverCar();
        this.noInsuranceCover = bi.getNoInsuranceCover();
        this.startAt = bi.getStartAt();
        this.endAt = bi.getEndAt();
        this.grade = bi.getGrade();
        this.isTransferred = bi.getIsTransferred();
        this.fee = bi.getFee();
        this.paidFee = bi.getPaidFee();
        this.createdUserNo = bi.getCreatedUserNo();
        this.createdAt = bi.getCreatedAt();
        this.updatedUserNo = bi.getUpdatedUserNo();
        this.updatedAt = bi.getUpdatedAt();
        this.paidUserNo = bi.getPaidUserNo();
        this.paidAt = bi.getPaidAt();
        this.description = bi.getDescription();
        this.penalty = bi.getPenalty();
        this.refund = bi.getRefund();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_no", nullable = false)
    private Long historyNo;

    @Column(name = "insurance_no", nullable = false)
    private Integer insuranceNo;

//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "insurance_no", insertable = false, updatable = false)
//    private BikeInsurances bikeInsurance;

    @Column(name = "insurance_id", nullable = false)
    private String insuranceId;

    // 증권번호
    @Column(name = "stock_number")
    private String stockNumber;

    @JsonIgnore
    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
//    private Bikes bike;

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

    @Column(name = "updated_no", length = 21)
    private Integer updatedUserNo;

    @ManyToOne
    @JoinColumn(name = "updated_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    private BikeUser updatedUser;

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "paid_user_no", length = 21)
    private Integer paidUserNo;

    @ManyToOne
    @JoinColumn(name = "paid_user_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    private BikeUser paidUser;

    @Column(name = "paid_at", columnDefinition = "DATETIME")
    private LocalDateTime paidAt;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "penalty")
    private Integer penalty;

    @Column(name = "refund")
    private Integer refund;

    public void setType (String type){
        this.type = InsuranceTypes.getInsuranceType(type);
    }

    public void setType (InsuranceTypes type){
        this.type = type;
    }

}
