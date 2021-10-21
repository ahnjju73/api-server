package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bike_riders")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeRidersBak extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx", nullable = false)
    private Long index;

    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "rider_no", nullable = false)
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Column(name = "rider_start_at")
    private LocalDateTime riderStartAt;

    @Column(name = "rider_end_at")
    private LocalDateTime riderEndAt;

    @Column(name = "rider_request_at")
    private LocalDateTime riderRequestAt;

    @Column(name = "rider_approval_at")
    private LocalDateTime riderApprovalAt;

    @Column(name = "rider_lease_no", nullable = false)
    private Integer riderLeaseNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_lease_no", insertable = false, updatable = false)
    private Leases riderLease;

}
