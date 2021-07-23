package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.BikeRiderStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeRiderStatusTypesConverter;
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
public class Bikes {

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

    @OneToMany(mappedBy = "bike", fetch = FetchType.LAZY)
    private List<Leases> lease = new ArrayList<>();

    @OneToMany(mappedBy = "bike", fetch = FetchType.LAZY)
    private List<BikeFileInfo> files = new ArrayList<>();

    @Column(name = "volume")
    private Integer volume;

    @Column(name = "usable")
    private Boolean usable = true;

    @Column(name = "rider_no")
    private Integer riderNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders riders;

    @Column(name = "rider_request_at")
    private LocalDateTime riderRequestAt;

    @Column(name = "rider_approval_at")
    private LocalDateTime riderApprovalAt;

    @Column(name = "rider_status", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = BikeRiderStatusTypesConverter.class)
    private BikeRiderStatusTypes riderStatus = BikeRiderStatusTypes.NONE;
}
