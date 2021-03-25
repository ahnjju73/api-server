package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
import helmet.bikelab.apiserver.domain.types.FineStatusTypes;
import helmet.bikelab.apiserver.domain.types.converters.ContractTypeConverter;
import helmet.bikelab.apiserver.domain.types.converters.FineStatusTypesConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "fines")
public class Fines {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fine_no", nullable = false)
    private Integer fineNo;

    @Column(name = "bike_no")
    private Integer bikeNo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bike_no", insertable = false, updatable = false)
    private Bikes bike;

    @Column(name = "fee")
    private Integer fee;

    @Column(name = "paid_fee")
    private Integer paidFee;

    @Column(name = "fine_num", length = 45)
    private String fineNum;

    @Column(name = "fine_date")
    private LocalDateTime fineDt;

    @Column(name = "fine_status", columnDefinition = "ENUM")
    @Convert(converter = FineStatusTypesConverter.class)
    private FineStatusTypes fineStatus;


}
