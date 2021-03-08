package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.types.PaymentTypes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_price")
public class LeasePrice {

    @Id
    @Column(name = "lease_no")
    private Integer leaseNo;

    @OneToOne(optional = false)
    @JoinColumn(name = "lease_no", nullable = false)
    private Leases lease;

    @Column(name = "payment_type", columnDefinition = "ENUM")
    private PaymentTypes type;

    // todo ask why varchar
    @Column(name = "payment_day", length = 45)
    private String paymentDay;

//    @Column(name = "payment_day", columnDefinition = "TinyInt")
//    private Integer paymentDay;
    @Column(name = "deposit")
    private Integer deposit;

    @Column(name = "pre_payment")
    private Integer prepayment;

    @Column(name = "total_lease_fee")
    private Integer totalLeaseFee;

    @Column(name = "profit_fee")
    private Integer profit;

    @Column(name = "take_fee")
    private Integer takeFee;

    @Column(name = "register_fee")
    private Integer registerFee;

}
