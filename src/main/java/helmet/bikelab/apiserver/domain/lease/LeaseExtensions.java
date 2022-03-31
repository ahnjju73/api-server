package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;
import helmet.bikelab.apiserver.domain.types.PaidTypes;
import helmet.bikelab.apiserver.domain.types.converters.LeaseStopStatusConverter;
import helmet.bikelab.apiserver.domain.types.converters.PaidTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_extensions")
public class LeaseExtensions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @Column(name = "lease_no", nullable = false)
    private Integer leaseNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lease_no", insertable = false, updatable = false)
    private Leases lease;

    @Column(name = "start_dt")
    private LocalDate start;

    @Column(name = "end_dt")
    private LocalDate endDate;

    @Column(name = "period")
    private Integer period;

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

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
