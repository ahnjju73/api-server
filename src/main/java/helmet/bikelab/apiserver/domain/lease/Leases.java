package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.ManagementTypes;
import helmet.bikelab.apiserver.domain.types.converters.ContractTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.LeaseStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.ManagementTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "leases")
public class Leases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lease_no", nullable = false)
    private Integer leaseNo;

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

    @OneToOne
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "release_no")
    private Integer releaseNo;

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
    private ContractTypes contractTypes = ContractTypes.OPERATING;

    @Column(name = "management_type", columnDefinition = "ENUM")
    @Convert(converter = ManagementTypeConverter.class)
    private ManagementTypes type = ManagementTypes.FINANCIAL;

    //505-001(인수), 002(반납)
    @Column(name = "expire_type")
    private String expireType;

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

    @OneToOne(mappedBy = "lease", optional = false)
    private LeaseInfo leaseInfo;

    @OneToOne(mappedBy = "lease", optional = false, fetch = FetchType.LAZY)
    private LeasePrice leasePrice;

    @OneToMany(mappedBy = "lease", fetch = FetchType.LAZY)
    private List<LeasePayments> payments = new ArrayList<>();

    @OneToMany(mappedBy = "lease", fetch = FetchType.LAZY)
    private List<LeaseExtras> extras = new ArrayList<>();
}
