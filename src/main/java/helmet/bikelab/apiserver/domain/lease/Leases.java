package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
import helmet.bikelab.apiserver.domain.types.ManagementTypes;
import helmet.bikelab.apiserver.domain.types.converters.ContractTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.ManagementTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

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

    @Column(name = "bike_no")
    private Integer bikeNo;

    @Column(name = "release_no")
    private Integer releaseNo;

    @Column(name = "insurance_no")
    private Integer insuranceNo;

    @Column(name = "contract_type", columnDefinition = "ENUM")
    @Convert(converter = ContractTypeConverter.class)
    private ContractTypes contractTypes;

    @Column(name = "management_type", columnDefinition = "ENUM")
    @Convert(converter = ManagementTypeConverter.class)
    private ManagementTypes type;

    @Column(name = "take_loc")
    private String takeLocation;

    @Column(name = "take_at")
    private LocalDateTime takeAt;

    @Column(name = "release_at")
    private LocalDateTime releaseAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // todo release, insurance mapping

    @OneToOne(mappedBy = "lease", optional = false)
    private LeaseInfo leaseInfo;

    @OneToOne
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @OneToOne
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @OneToOne(mappedBy = "lease", optional = false)
    private LeasePrice leasePrice;

    @OneToMany(mappedBy = "lease")
    List<LeasePayments> payments = new ArrayList<>();

    @OneToMany(mappedBy = "lease")
    List<LeaseExtras> extras = new ArrayList<>();
}
