package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeasePaymentDto extends OriginObject {
    private String paymentId;
    private Integer idx;
    private LocalDate paymentDate;
    private LocalDate paymentEndDate;
    private Integer leaseFee;
    private Integer paidFee;
    private Integer unpaidFee;
    private String paidType;
    private String description;
    private ClientDto payClient;


    public boolean equals(LeasePayments leasePayments){
        return paymentId.equals(leasePayments.getPaymentId());
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        if(bePresent(paymentDate)) this.paymentDate = LocalDate.parse(paymentDate);
    }

    public void setPaymentEndDate(String paymentEndDate) {
        if(bePresent(paymentEndDate)) this.paymentEndDate = LocalDate.parse(paymentEndDate);
    }

    public void setPaymentEndDate(LocalDate paymentEndDate) {
        this.paymentEndDate = paymentEndDate;
    }
}
