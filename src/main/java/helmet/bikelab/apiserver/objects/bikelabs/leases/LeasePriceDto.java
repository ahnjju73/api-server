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
    private String paymentDay;
    private Integer deposit;
    private Integer prePayment;
    private Integer totalLeaseFee;
    private Integer profitFee;
    private Integer takeFee;
    private Integer registerFee;

    public void setLeasePrice(LeasePrice leasePrice){
        paymentDay = leasePrice.getPaymentDay();
        paymentType = leasePrice.getType().getPaymentType();
        deposit = leasePrice.getDeposit();
        prePayment = leasePrice.getPrepayment();
        totalLeaseFee = leasePrice.getTotalLeaseFee();
        profitFee = leasePrice.getRegisterFee();
        takeFee = leasePrice.getTakeFee();
        registerFee = leasePrice.getRegisterFee();
    }
}
