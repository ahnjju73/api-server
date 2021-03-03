package helmet.bikelab.apiserver.domain.bike;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bike_payment")
public class BikePayments {
    @Id
    @Column(name = "bike_no")
    private Integer bikeNo;

    @Column(name = "reg_fee")
    private Integer regstrationFee;

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
