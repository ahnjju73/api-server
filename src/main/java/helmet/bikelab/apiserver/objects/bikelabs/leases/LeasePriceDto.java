package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.lease.LeasePrice;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeasePriceDto {
    private String paymentType;
    private Integer deposit;
    private Integer prePayment;
    private Integer leaseFee;
    private Integer profitFee;
    private Integer takeFee;
    private Integer registerFee;

    public void setLeasePrice(LeasePrice leasePrice){
        paymentType = leasePrice.getType().getPaymentType();
        deposit = leasePrice.getDeposit();
        prePayment = leasePrice.getPrepayment();
        profitFee = leasePrice.getProfit();
        takeFee = leasePrice.getTakeFee();
        registerFee = leasePrice.getRegisterFee();
    }
}
