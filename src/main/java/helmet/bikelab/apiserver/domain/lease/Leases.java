package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.domain.types.converters.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "leases")
public class   Leases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lease_no", nullable = false)
    private Integer leaseNo;

    @JsonIgnore
    @Column(name = "up_lease_no")
    private Integer upLesase;

    @Column(name = "lease_id", unique = true, length = 21)
    private String leaseId;

    @Column(name = "client_no")
    private Integer clientNo;

    @ManyToOne
    @JoinColumn(name = "client_no", insertable = false, updatable = false, unique = true)
    private Clients clients;

    @Column(name = "bike_no")
    private Integer bikeNo;

    @Column(name = "bak_bike_no")
    private Integer bakBikeNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "release_no")
    private Integer releaseNo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "release_no", insertable = false, updatable = false)
    private Releases releases;

    @Column(name = "insurance_no")
    private Integer insuranceNo;

    @ManyToOne
    @JoinColumn(name = "insurance_no", insertable = false, updatable = false)
    private Insurances insurances;

    @Column(name = "contract_type", columnDefinition = "ENUM")
    @Convert(converter = ContractTypeConverter.class)
    private ContractTypes contractTypes = ContractTypes.MANAGEMENT;

    @Column(name = "management_type", columnDefinition = "ENUM")
    @Convert(converter = ManagementTypeConverter.class)
    private ManagementTypes type = ManagementTypes.FINANCIAL;

    @Column(name = "expire_type", columnDefinition = "ENUM")
    @Convert(converter = ExpireTypesConverter.class)
    private ExpireTypes expireTypes = ExpireTypes.TAKE_OVER;

    @Column(name = "take_loc")
    private String takeLocation;

    @Column(name = "take_at")
    private LocalDateTime takeAt;

    @Column(name = "release_at")
    private LocalDateTime releaseAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status", columnDefinition = "ENUM")
    @Convert(converter = LeaseStatusTypesConverter.class)
    private LeaseStatusTypes status = LeaseStatusTypes.IN_PROGRESS;

    @Column(name = "created_user_no")
    private Integer createdUserNo;

    @ManyToOne
    @JoinColumn(name = "created_user_no", insertable = false, updatable = false)
    private BikeUser createdUser;

    @Column(name = "submitted_user_no")
    private Integer submittedUserNo;

    @ManyToOne
    @JoinColumn(name = "submitted_user_no", insertable = false, updatable = false)
    private BikeUser submittedUser;

    @Column(name = "approval_user_no")
    private Integer approvalUserNo;

    @ManyToOne
    @JoinColumn(name = "approval_user_no", insertable = false, updatable = false)
    private BikeUser approvalUser;

    //stop_lease
    @Column(name = "lease_stop_status", columnDefinition = "ENUM")
    @Convert(converter = LeaseStopStatusConverter.class)
    private LeaseStopStatusTypes leaseStopStatus = LeaseStopStatusTypes.CONTINUE;

    @Column(name = "lease_stop_status", insertable = false, updatable = false)
    private String leaseStopStatusCode;

    @Column(name = "stop_dt")
    private LocalDateTime stopDt;

    @Column(name = "stop_fee")
    private Long stopFee;

    @Column(name = "stop_paid_fee")
    private Long stopPaidFee;

    @Column(name = "stop_reason", columnDefinition = "MEDIUMTEXT")
    private String stopReason;

    @Column(name = "approval_dt")
    private LocalDateTime approvalDt;

    @Column(name = "is_mt", columnDefinition = "TINYINT(1)")
    private Boolean isMt = false;

    @Column(name = "demand_lease_no")
    private Long demandLeaseNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demand_lease_no", insertable = false, updatable = false)
    private DemandLeases demandLeases;

    @OneToOne(mappedBy = "lease", optional = false)
    private LeaseInfo leaseInfo;

    @OneToOne(mappedBy = "lease", optional = false, fetch = FetchType.EAGER)
    private LeasePrice leasePrice;

    @OneToOne(mappedBy = "lease")
    private LeaseInsurances leaseInsurance;

    @JsonIgnore
    @OneToMany(mappedBy = "lease", fetch = FetchType.LAZY)
    private List<LeasePayments> payments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "lease", fetch = FetchType.LAZY)
    private List<LeaseExtras> extras = new ArrayList<>();

    @OneToOne(mappedBy = "lease", fetch = FetchType.EAGER)
    private LeaseAttachments attachments;


    public void setExtensionLease(){
        setBikeNo(getBakBikeNo());
        leaseStopStatus = LeaseStopStatusTypes.CONTINUE;
        stopDt = null;
        stopPaidFee = null;
        stopFee = null;
        stopReason = null;
    }
}
