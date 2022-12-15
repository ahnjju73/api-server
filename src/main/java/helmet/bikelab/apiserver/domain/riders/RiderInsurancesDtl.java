package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.domain.types.converters.*;
import helmet.bikelab.apiserver.objects.AddressDto;
import helmet.bikelab.apiserver.objects.BankInfoDto;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(name = "rider_insurances_dtl")
public class RiderInsurancesDtl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dtl_no")
    private Integer dtlNo;

    @Column(name = "rider_ins_no")
    private Integer riderInsNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_ins_no", insertable = false, updatable = false)
    private RiderInsurances riderInsurances;

    @Column(name = "insurance_company", columnDefinition = "ENUM")
    @Convert(converter = InsCompanyTypeConverter.class)
    private InsCompanyTypes insCompany = InsCompanyTypes.KB;

    @Column(name = "insurance_company", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String insCompanyCode;

    @Column(name = "insurance_number")
    private String insNum;

    @Column(name = "status", columnDefinition = "ENUM")
    @Convert(converter = RiderInsuranceStatusConverter.class)
    private RiderInsuranceStatus riderInsuranceStatus;

    @Column(name = "ins_range", columnDefinition = "ENUM")
    @Convert(converter = InsRangeTypeConverter.class)
    private InsRangeTypes insRangeType;

    @Column(name = "ins_range", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String insRangeTypeCode;

    @Column(name = "bank_info", columnDefinition = "JSON")
    @Convert(converter = BankInfoDtoConverter.class)
    private BankInfoDto bankInfo;

    @Column(name = "`usage`", columnDefinition = "ENUM")
    @Convert(converter = UsageTypeConverter.class)
    private UsageTypes usageTypes = UsageTypes.COST_DELIVERY;

    @Column(name = "`usage`", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String usageTypeCode;

    @Column(name = "bike_num")
    private String bikeNum;

    @Column(name = "vim_num")
    private String vimNum;

    @Column(name = "bike_type", columnDefinition = "ENUM")
    @Convert(converter = InsuranceBikeTypeConverter.class)
    private InsuranceBikeTypes bikeTypes;

    @Column(name = "bike_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String bikeTypesCode;

    @Column(name = "additional_standard", columnDefinition = "ENUM")
    @Convert(converter = AdditionalStandardTypeConverter.class)
    private AdditionalStandardTypes additionalStandardTypes;

    @Column(name = "additional_standard", columnDefinition = "ENUM", updatable = false, insertable = false)
    private Integer additionalStandardTypeCode;

    @Column(name = "start_dt")
    private LocalDateTime startDt;

    @Column(name = "end_dt")
    private LocalDateTime endDt;

    @Column(name = "stop_dt")
    private LocalDateTime stopDt;

    @Column(name = "ins_fee")
    private Integer insFee;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "liability_man")
    private Integer liabilityMan;

    @Column(name = "liability_car")
    private Integer liabilityCar;

    @Column(name = "liability_man2")
    private Integer liabilityMan2;

    @Column(name = "self_cover_man")
    private Integer selfCoverMan;

    @Column(name = "self_cover_car")
    private Integer selfCoverCar;

    @Column(name = "no_insurance_cover")
    private Integer noInsCover;

    @Column(name = "rider_age", columnDefinition = "ENUM")
    @Convert(converter = InsAgeTypeConverter.class)
    private InsAgeTypes age;

    @Column(name = "rider_age", columnDefinition = "ENUM",insertable = false, updatable = false)
    private String ageCode;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;
}
