package helmet.bikelab.apiserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.ins_companies.InsuranceCompanies;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "custom_estimates", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomEstimates {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_estimate_no")
    private Long customEstimateNo;

    @Column(name = "custom_estimate_id", length = 21, unique = true, nullable = false)
    private String customEstimateId;

    @Column(name = "ins_company_no")
    private Integer insCompanyNo;

    @Column(name = "ins_num")
    private String insuranceNumber;

    @Column(name = "dept_no")
    private Integer deptNo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ins_company_no", insertable = false, updatable = false)
    private InsuranceCompanies insCompany;

    @Column(name = "bike_number", length = 45, unique = true)
    private String bikeNum;

    @Column(name = "bike_model", length = 21)
    private String bikeModelCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bike_model", insertable = false, updatable = false)
    private CommonBikes bikeModel;

    @Column(name = "rate_accident")
    private Integer rateAccident;

    @OneToMany(mappedBy = "customEstimates", fetch = FetchType.EAGER)
    private List<CustomProvisionalEstimates> provisionalEstimates;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
