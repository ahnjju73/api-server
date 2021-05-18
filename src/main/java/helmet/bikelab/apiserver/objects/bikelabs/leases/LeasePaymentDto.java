package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeasePaymentDto {
    private String paymentId;
    private Integer idx;
    private LocalDate paymentDate;
    private Integer leaseFee;
    private Integer paidFee;
    private Integer unpaidFee;

    public boolean equals(LeasePayments leasePayments){
        return paymentId.equals(leasePayments.getPaymentId());
    }
}