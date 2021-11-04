package helmet.bikelab.apiserver.domain.riders;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.domain.types.converters.*;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "rider_demand_lease_histories")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderDemandLeaseHistories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_no")
    private Integer historyNo;

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

    @Column(name = "chat_url", length = 512)
    private String chatUrl;

    @Column(name = "attachments_history", columnDefinition = "json")
    @Convert(converter = RiderDemandAttachmentBackUpConverter.class)
    private List<RiderDemandLeaseAttachments> attachmentsHistory;

    @Column(name = "terms_history", columnDefinition = "json")
    @Convert(converter = RiderDemandTermBackUpConverter.class)
    private List<RiderDemandLeaseSpecialTerms> termsHistory;


    public void setHistory(RiderDemandLease riderDemandLease){
        riderNo = riderDemandLease.getRiderNo();
        leaseNo = riderDemandLease.getLeaseNo();
        carModelCode = riderDemandLease.getCarModelCode();
        demandLeaseStatusTypes = riderDemandLease.getDemandLeaseStatusTypes();
        managementType = riderDemandLease.getManagementType();
        period = riderDemandLease.getPeriod();
        expireTypes = riderDemandLease.getExpireTypes();
        prepayment = riderDemandLease.getPrepayment();
        paymentType = riderDemandLease.getPaymentType();
        insuranceType = riderDemandLease.getInsuranceType();
        isMaintenance = riderDemandLease.getIsMaintenance();
        createdAt = riderDemandLease.getCreatedAt();
        pendingAt = riderDemandLease.getPendingAt();
        chatUrl = riderDemandLease.getChatUrl();
    }

}
