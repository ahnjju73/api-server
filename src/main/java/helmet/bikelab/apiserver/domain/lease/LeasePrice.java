package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.types.PaymentTypes;
import helmet.bikelab.apiserver.domain.types.converters.PaymentTypeConverter;
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

    @JsonIgnore
    @OneToOne(optional = false)
    @JoinColumn(name = "lease_no", nullable = false)
    private Leases lease;

    @Column(name = "payment_type", columnDefinition = "ENUM")
    @Convert(converter = PaymentTypeConverter.class)
    private PaymentTypes type = PaymentTypes.MONTHLY;

    @Column(name = "deposit")
    private Integer deposit = 0;

    @Column(name = "pre_payment")
    private Integer prepayment = 0;

    @Column(name = "profit_fee")
    private Integer profit = 0;

    @Column(name = "take_fee")
    private Integer takeFee = 0;

    @Column(name = "register_fee")
    private Integer registerFee = 0;

}
