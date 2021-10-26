package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.domain.types.converters.*;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "rider_demand_leases", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderDemandLease extends OriginObject {

    @Id
    @JsonIgnore
    @Column(name = "rider_no")
    private Integer riderNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "lease_no")
    private Integer leaseNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lease_no", insertable = false, updatable = false)
    private Leases lease;

    @Column(name = "car_model", length = 21)
    private String carModelCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "car_model", insertable = false, updatable = false)
    private CommonCodeBikes carModel;

    @Column(name = "demand_lease_status", columnDefinition = "ENUM")
    @Convert(converter = DemandLeaseStatusTypesConverter.class)
    private DemandLeaseStatusTypes demandLeaseStatusTypes = DemandLeaseStatusTypes.IN_PROGRESS;

    @Column(name = "demand_lease_status", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String demandLeaseStatusTypeCode;

    @Column(name = "management_type", columnDefinition = "ENUM")
    @Convert(converter = ManagementTypeConverter.class)
    private ManagementTypes managementType;

    @Column(name = "management_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String managementTypeCode;

    @Column(name = "period", columnDefinition = "TINYINT(3)")
    private Integer period;

    @Column(name = "expire_type", columnDefinition = "ENUM")
    @Convert(converter = ExpireTypesConverter.class)
    private ExpireTypes expireTypes;

    @Column(name = "expire_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String expireTypeCode;

    @Column(name = "pre_payment")
    private Integer prepayment = 0;

    @Column(name = "payment_type", columnDefinition = "ENUM")
    @Convert(converter = PaymentTypeConverter.class)
    private PaymentTypes paymentType;

    @Column(name = "insurance_type", columnDefinition = "ENUM")
    @Convert(converter = RiderDemandLeaseTypesConverter.class)
    private RiderDemandLeaseTypes insuranceType;

    @Column(name = "insurance_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String insuranceTypeCode;

    @Column(name = "payment_type", columnDefinition = "ENUM", insertable = false, updatable = false)
    private String paymentTypeCode;

    @Column(name = "is_maintenance", columnDefinition = "TINYINT(1)")
    private Boolean isMaintenance = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "pending_at")
    private LocalDateTime pendingAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "reject_message", columnDefinition = "MEDIUMTEXT")
    private String rejectMessage;

    @Column(name = "chat_url", length = 512)
    private String chatUrl;

    public void checkToRequestApproval(){
        if(!bePresent(this.carModel)) withException("3200-009");
        if(!bePresent(this.period)) withException("3200-004");
        if(!bePresent(this.insuranceType)) withException("3200-005");
        if(!bePresent(this.expireTypes)) withException("3200-006");
        if(!bePresent(this.managementType)) withException("3200-007");
        if(!bePresent(this.paymentType)) withException("3200-008");
    }

//    @JsonIgnore
//    @OneToMany(mappedBy = "demandLeases", fetch = FetchType.EAGER)
//    private List<DemandLeaseAttachments> attachments = new ArrayList<>();

}
