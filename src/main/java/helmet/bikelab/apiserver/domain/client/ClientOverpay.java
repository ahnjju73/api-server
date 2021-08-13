package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@Table(name = "client_overpay")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientOverpay extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "overpay_no")
    private Long overpayNo;

    @Column(name = "lease_no")
    private Integer LeaseNo;

    @Column(name = "client_no")
    private Integer clientNo;

    @Column(name = "overpay_fee")
    private Integer overpayFee;

    @Column(name = "refund_fee")
    private Integer refundFee = 0;

    @Column(name = "date")
    private LocalDateTime date;

}
