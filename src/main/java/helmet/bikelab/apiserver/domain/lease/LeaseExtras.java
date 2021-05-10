package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.types.ExtraTypes;
import helmet.bikelab.apiserver.domain.types.converters.ExtraTypeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_extra")
public class LeaseExtras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "extra_no", nullable = false)
    private Integer extraNo;

    @Column(name = "payment_no", nullable = false)
    private Integer paymentNo;

    @Column(name = "extra_id")
    private String extraId;

    @OneToOne(optional = false)
    @JoinColumn(name = "payment_no", nullable = false, insertable = false, updatable = false)
    private LeasePayments payment;

    @Column(name = "lease_no", nullable = false)
    private Integer leaseNo;

    @ManyToOne
    @JoinColumn(name = "lease_no", insertable = false, updatable = false)
    private Leases lease;

    @Column(name = "extra_type", nullable = false, columnDefinition = "ENUM")
    @Convert(converter = ExtraTypeConverter.class)
    private ExtraTypes extraTypes;

    @Column(name = "extra_fee")
    private Integer extraFee;

    @Column(name = "paid_fee", nullable = false)
    private Integer paidFee = 0;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;



}
