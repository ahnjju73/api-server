package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.types.InsRangeTypes;
import helmet.bikelab.apiserver.domain.types.RiderInsuranceStatus;
import helmet.bikelab.apiserver.domain.types.converters.BankInfoDtoConverter;
import helmet.bikelab.apiserver.domain.types.converters.InsRangeTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.RiderInfoDtoConverter;
import helmet.bikelab.apiserver.domain.types.converters.RiderInsuranceStatusConverter;
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
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_ins_no", insertable = false, updatable = false)
    private RiderInsurances riderInsurances;

    @Column(name = "insurance_company")
    private String insCompany;

    @Column(name = "insurance_number")
    private String insNum;

    @Column(name = "rider_info", columnDefinition = "JSON")
    @Convert(converter = RiderInfoDtoConverter.class)
    private RiderInfoDto riderInfoDto;

    @Column(name = "rider_info", columnDefinition = "JSON", updatable = false, insertable = false)
    private String riderInfo;

    @Column(name = "status", columnDefinition = "ENUM")
    @Convert(converter = RiderInsuranceStatusConverter.class)
    private RiderInsuranceStatus riderInsuranceStatus;

    @Column(name = "ins_range", columnDefinition = "ENUM")
    @Convert(converter = InsRangeTypeConverter.class)
    private InsRangeTypes insRangeType;

    @Column(name = "bank_info", columnDefinition = "JSON")
    @Convert(converter = BankInfoDtoConverter.class)
    private BankInfoDto bankInfo;

    @Column(name = "`usage`")
    private String usage;

    @Column(name = "additional_standard")
    private Integer additionalStandard;

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

}
