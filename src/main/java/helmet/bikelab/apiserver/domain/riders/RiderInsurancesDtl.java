package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private InsCompanyTypes insCompany;

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

    @Column(name = "usage", columnDefinition = "ENUM")
    @Convert(converter = UsageTypeConverter.class)
    private UsageTypes usageTypes;

    @Column(name = "usage", columnDefinition = "ENUM", updatable = false, insertable = false)
    private String usageTypeCode;

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

    @Column(name = "stop_req_dt")
    private LocalDateTime stopReqDt;

    @Column(name = "ins_fee")
    private Integer insFee;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

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

}
