package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.embeds.ModelBikeTransaction;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.BikeRiderStatusTypes;
import helmet.bikelab.apiserver.domain.types.BikeStatusTypes;
import helmet.bikelab.apiserver.domain.types.PayerTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeRiderStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.BikeStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.PayerTypesConverter;
import helmet.bikelab.apiserver.objects.requests.UploadBikeInfo;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Bikes extends OriginObject {

    public Bikes(){}

    public Bikes(UploadBikeInfo bikeInfo, String bikeId){
        this.bikeId = bikeId;
        this.bikeStatus = bikeInfo.getStatus();
        this.vimNum = bikeInfo.getVimNum();
        this.carNum = bikeInfo.getNumber();
        this.color = bikeInfo.getColor();
        this.receiveDate = bikeInfo.getReceiveDt();
        this.odometerByAdmin = bikeInfo.getOdometerByAdmin();
        this.transaction = new ModelBikeTransaction(bikeInfo.getRegNum(), bikeInfo.getCompanyName(), bikeInfo.getPrice());
        this.description = bikeInfo.getDescription();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bike_no", nullable = false)
    private Integer bikeNo;

    @Column(name = "bike_id", length = 45, unique = true)
    private String bikeId;

    @Column(name = "status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = BikeStatusTypesConverter.class)
    private BikeStatusTypes bikeStatus = BikeStatusTypes.PENDING;

    @Column(name = "insurance_no", nullable = false)
    private Integer bikeInsuranceNo;

    @ManyToOne
    @JoinColumn(name = "insurance_no", insertable = false, updatable = false)
    private BikeInsurances bikeInsurance;

    @Column(name = "vim_num", length = 45, unique = true)
    private String vimNum;

    @Column(name = "number", length = 45, unique = true)
    private String carNum;

    @Column(name = "car_model", length = 21)
    private String carModelCode;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_model", insertable = false, updatable = false)
    private CommonBikes carModel;

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

    @OneToMany(mappedBy = "bike", fetch = FetchType.EAGER)
    private List<BikeAttachments> files = new ArrayList<>();

    @Column(name = "odometer_by_admin")
    private Integer odometerByAdmin = 0;

    @Column(name = "usable")
    private Boolean usable = true;

    @Column(name = "is_bm", columnDefinition = "TINYINT(1)")
    private Boolean isBikemaster = true;

    @Column(name = "payer_types", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = PayerTypesConverter.class)
    private PayerTypes payerType = PayerTypes.COMPANY;

    @Column(name = "payer_types", columnDefinition = "ENUM", nullable = false, insertable = false, updatable = false)
    private String payerTypeCode;

    @Embedded
    private ModelBikeTransaction transaction = new ModelBikeTransaction();

    @Column(name = "rider_no")
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.EAGER)
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_lease_no", insertable = false, updatable = false)
    private Leases riderLease;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "warehouse")
    private String warehouse;

    @Column(name = "deleted_at", columnDefinition = "DATETIME")
    private LocalDateTime deletedAt;

    public void doApproveRider(){
        this.riderStatus = BikeRiderStatusTypes.TAKEN;
        this.riderApprovalAt = LocalDateTime.now();
    }

    public void doDeclineRider(){
        this.riderStatus = BikeRiderStatusTypes.NONE;
        this.riderNo = null;
        this.riders = null;
        this.riderLeaseNo = null;
        this.riderLease = null;
        this.riderStartAt = null;
        this.riderEndAt = null;
        this.riderApprovalAt = null;
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
        if(bePresent(leases)) this.riderLeaseNo = leases.getLeaseNo();
    }

    public void setCarModelData(CommonBikes carModel){
        this.carModelCode = carModel.getCode();
        this.years = carModel.getYear();
    }

    public void setUsable(Boolean usable) {
        this.usable = usable;
        if(usable){
            this.deletedAt = null;
        }else {
            this.deletedAt = LocalDateTime.now();
            this.warehouse = null;
        }
    }


}
