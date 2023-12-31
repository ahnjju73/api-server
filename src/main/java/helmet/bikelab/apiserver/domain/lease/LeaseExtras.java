package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "payment_no", nullable = false, insertable = false, updatable = false)
    private LeasePayments payment;

    @Column(name = "lease_no", nullable = false)
    private Integer leaseNo;

    @ManyToOne(optional = false)
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

    @Column(name = "read_user_no", nullable = false)
    private Integer readUserNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "read_user_no", insertable = false, updatable = false)
    private BikeUser readUser;

    @Column(name = "read_yn")
    private Boolean read = false;

}
