package helmet.bikelab.apiserver.domain.demands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.Insurances;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.DemandLeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.ExpireTypes;
import helmet.bikelab.apiserver.domain.types.ManagementTypes;
import helmet.bikelab.apiserver.domain.types.PaymentTypes;
import helmet.bikelab.apiserver.domain.types.converters.DemandLeaseStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.ExpireTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.ManagementTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.PaymentTypeConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "demand_leases", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
public class DemandLeases extends OriginObject {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "demand_lease_no")
    private Long demandLeaseNo;

    @Column(name = "demand_lease_id", length = 21, unique = true, nullable = false)
    private String demandLeaseId;

    @JsonIgnore
    @Column(name = "client_no", nullable = false)
    private Integer clientNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JsonIgnore
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @JsonIgnore
    @Column(name = "lease_no")
    private Integer leaseNo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "lease_no", insertable = false, updatable = false)
    private Leases lease;

    @Column(name = "car_model", length = 21)
    private String carModelCode;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "car_model", insertable = false, updatable = false)
    private CommonCodeBikes carModel;

    @Column(name = "demand_lease_status", columnDefinition = "ENUM")
    @Convert(converter = DemandLeaseStatusTypesConverter.class)
    private DemandLeaseStatusTypes demandLeaseStatusTypes = DemandLeaseStatusTypes.PENDING;

    @Column(name = "color", length = 45)
    private String color;

    @Column(name = "management_type", columnDefinition = "ENUM")
    @Convert(converter = ManagementTypeConverter.class)
    private ManagementTypes managementType = ManagementTypes.FINANCIAL;

    @Column(name = "period")
    private Integer period;

    @Column(name = "expire_type", columnDefinition = "ENUM")
    @Convert(converter = ExpireTypesConverter.class)
    private ExpireTypes expireTypes = ExpireTypes.TAKE_OVER;

    @Column(name = "pre_payment")
    private Integer prepayment = 0;

    @Column(name = "payment_type", columnDefinition = "ENUM")
    @Convert(converter = PaymentTypeConverter.class)
    private PaymentTypes paymentType = PaymentTypes.MONTHLY;

    @Column(name = "fee")
    private Integer fee = 0;

    @JsonIgnore
    @Column(name = "insurance_no", nullable = false)
    private Integer insuranceNo;

    @ManyToOne
    @JoinColumn(name = "insurance_no", insertable = false, updatable = false)
    private Insurances insurance;

    @Column(name = "is_maintenance")
    private Boolean isMaintenance = true;

    @Column(name = "extra_service", columnDefinition = "MEDIUMTEXT")
    private String extraService;

    @Column(name = "extra_info", columnDefinition = "MEDIUMTEXT")
    private String extraInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "pending_at")
    private LocalDateTime pendingAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

}
