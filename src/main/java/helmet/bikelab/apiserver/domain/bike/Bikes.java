package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.BikeRiderStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeRiderStatusTypesConverter;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "bikes")
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Bikes extends OriginObject {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @Column(name = "bike_id", length = 45, unique = true)
    private String bikeId;

    @Column(name = "vim_num", length = 45, unique = true)
    private String vimNum;

    @Column(name = "number", length = 45, unique = true)
    private String carNum;

    @Column(name = "car_model", length = 21)
    private String carModelCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_model", insertable = false, updatable = false)
    private CommonCodeBikes carModel;

    @Column(name = "years")
    private Integer years;

    @Column(name = "color", length = 45)
    private String color;

    @Column(name = "receive_dt")
    private LocalDateTime receiveDate;

    @Column(name = "register_dt")
    private LocalDateTime registerDate;

    @JsonIgnore
    @OneToMany(mappedBy = "bike", fetch = FetchType.LAZY)
    private List<Leases> lease = new ArrayList<>();

    @OneToMany(mappedBy = "bike", fetch = FetchType.LAZY)
    private List<BikeAttachments> files = new ArrayList<>();

    @Column(name = "volume")
    private Integer volume;

    @Column(name = "usable")
    private Boolean usable = true;

    @Embedded
    private ModelTransaction transaction = new ModelTransaction();

    @Column(name = "rider_no")
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders riders;

    @Column(name = "rider_start_at")
    private LocalDateTime riderStartAt;

    @Column(name = "rider_end_at")
    private LocalDateTime riderEndAt;

    @Column(name = "rider_request_at")
    private LocalDateTime riderRequestAt;

    @Column(name = "rider_approval_at")
    private LocalDateTime riderApprovalAt;

    @Column(name = "rider_status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = BikeRiderStatusTypesConverter.class)
    private BikeRiderStatusTypes riderStatus = BikeRiderStatusTypes.NONE;

    @Column(name = "rider_lease_no", nullable = false)
    private Integer riderLeaseNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_lease_no", insertable = false, updatable = false)
    private Leases riderLease;

    public void doApproveRider(){
        this.riderStatus = BikeRiderStatusTypes.TAKEN;
        this.riderApprovalAt = LocalDateTime.now();
    }

    public void doDeclineRider(){
        this.riderStatus = BikeRiderStatusTypes.NONE;
        this.riderApprovalAt = null;
        this.riderNo = null;
        this.riders = null;
        this.riderLeaseNo = null;
        this.riderLease = null;
    }

    public void isRidable(){
        if(this.riderNo != null) withException("510-002");
        if(this.riderStatus != null && !BikeRiderStatusTypes.NONE.equals(this.riderStatus)) withException("510-002");
    }

    public void assignRider(Riders rider, LocalDateTime startAt, LocalDateTime endAt, Leases leases){
        this.riders = rider;
        this.riderNo = rider.getRiderNo();
        this.setRiderStatus(BikeRiderStatusTypes.TAKEN);
        this.setRiderStartAt(startAt);
        this.setRiderEndAt(endAt);
        this.setRiderApprovalAt(LocalDateTime.now());
        this.setRiderRequestAt(LocalDateTime.now());
        this.riderLeaseNo = leases.getLeaseNo();
    }
}
