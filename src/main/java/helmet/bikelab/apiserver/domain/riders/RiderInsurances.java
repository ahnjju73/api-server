package helmet.bikelab.apiserver.domain.riders;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "rider_insurances")
public class RiderInsurances extends OriginObject {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rider_ins_no")
    private Integer riderInsNo;

    @Column(name = "rider_no")
    private Integer riderNo;

    @Column(name = "rider_ins_id")
    private String riderInsId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @OneToOne(mappedBy = "riderInsurances", optional = false)
    private RiderInsurancesDtl riderInsurancesDtl;

    @Column(name = "bike_num")
    private String bikeNum;

    @Column(name = "vim_num")
    private String vimNum;

    @Column(name = "bike_no")
    private Integer bikeNo;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bikes;
}
