package helmet.bikelab.apiserver.domain.lease;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lease_payments")
public class LeasePayments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_no")
    private Integer paymentNo;

    @Column(name = "payment_id", nullable = false, unique = true, length = 21)
    private String paymentId;

    @Column(name = "lease_no", nullable = false)
    private Integer leaseNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lease_no", insertable = false, updatable = false)
    private Leases lease;

    @Column(name = "index", columnDefinition = "TINYINT")
    private Integer index;

    @Column(name = "payment_date")
    private LocalDate paymentDt;

    @Column(name = "lease_fee", nullable = false)
    private Integer leaseFee;

    @Column(name = "paid_fee", nullable = false)
    private Integer paidFee = 0;

    @Column(name = "inserted_at", nullable = false)
    private LocalDateTime insertDt;

    @Column(name = "inserted_user_no", nullable = false)
    private Integer insertedUserNo;



}