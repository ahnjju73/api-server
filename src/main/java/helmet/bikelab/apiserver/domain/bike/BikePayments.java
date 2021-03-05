package helmet.bikelab.apiserver.domain.bike;

import helmet.bikelab.apiserver.domain.client.Clients;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bike_payment")
public class BikePayments {
    @Id
    @Column(name = "bike_no")
    private Integer bikeNo;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bikes;

    @Column(name = "reg_fee")
    private Integer registrationFee;

    @Column(name = "bm_care")
    private Integer bmCare;

    @Column(name = "supplies_fee")
    private Integer supplyFee;

    @Column(name = "bike_price")
    private Integer bikePrice;

    @Column(name = "carrier_price")
    private Integer carrierPrice;

    @Column(name = "box_price")
    private Integer boxPrice;
}
