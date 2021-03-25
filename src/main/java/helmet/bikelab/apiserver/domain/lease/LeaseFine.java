package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.types.LeaseFinePK;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(LeaseFinePK.class)
@Table(name = "lease_fine")
public class LeaseFine {
    @Id
    @Column(name = "payment_no", nullable = false)
    private Integer paymentNo;

    @ManyToOne
    @JoinColumn(name = "payment_no", insertable = false, updatable = false)
    private LeasePayments payments;

    @Id
    @Column(name = "fine_no", nullable = false)
    private Integer fineNo;

    @OneToOne
    @JoinColumn(name = "fine_no", insertable = false, updatable = false)
    private Fines fine;

    @Column(name = "lease_no")
    private Integer leaseNo;

    @ManyToOne
    @JoinColumn(name = "lease_no", insertable = false, updatable = false)
    private Leases lease;
}
