package helmet.bikelab.apiserver.domain.lease;

import helmet.bikelab.apiserver.domain.bike.Bikes;
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

    @Column(name = "fine_num", length = 45)
    private String fineNum;

    @Column(name = "fine_date")
    private LocalDateTime fineDt;

}
