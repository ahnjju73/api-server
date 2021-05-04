package helmet.bikelab.apiserver.domain.lease;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Fines {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fine_no", nullable = false)
    private Integer fineNo;

    @Column(name = "fine_id")
    private String fineId;

    @Column(name = "fee")
    private Integer fee;

    @Column(name = "paid_fee")
    private Integer paidFee;

    @Column(name = "fine_num", length = 45)
    private String fineNum;

    @Column(name = "fine_date")
    private LocalDateTime fineDt;

    @Column(name = "fine_expire_date")
    private LocalDateTime expireDt;

}
