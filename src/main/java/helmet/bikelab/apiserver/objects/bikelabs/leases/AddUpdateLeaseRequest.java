package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import helmet.bikelab.apiserver.objects.requests.StopLeaseDto;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateLeaseRequest extends OriginObject {
    private String leaseId;
    private String bikeId;
    private String clientId;
    private String insuranceId;
    private String releaseId;
    private LeaseInfoDto leaseInfo;
    private LeasePriceDto leasePrice;
    private Integer upLeaseNo;
    private String contractType;
    private String managementType;
    private String takeLoc;
    private LocalDateTime takeAt;
    private LocalDateTime releaseAt;
    private List<LeasePaymentDto> leasePayments = new ArrayList<>();
    private StopLeaseDto stopLeaseInfo;
    private Boolean isMt;

    public void validationCheck(){
        if(contractType == null) withException("850-037");
        if(managementType == null) withException("850-030");
        if(insuranceId == null) withException("850-013");
        if(clientId == null) withException("850-012");
        if(bikeId == null) withException("850-010");
        if(leasePrice.getPaymentType() == null) withException("850-014");
        if(leaseInfo.getPeriod() == null) withException("850-019");
        if(leasePrice.getLeaseFee() == null || leasePrice.getLeaseFee() == 0) withException("850-036");
        if(leaseInfo.getContractDt() == null) withException("850-016");
        if(leaseInfo.getStartDt() == null) withException("850-017");
    }

}
