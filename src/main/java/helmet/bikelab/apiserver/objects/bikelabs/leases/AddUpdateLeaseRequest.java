package helmet.bikelab.apiserver.objects.bikelabs.leases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
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

    public void setTakeAt(String takeAt) {
        if(takeAt != null)
            this.takeAt = LocalDateTime.parse(takeAt);
    }

    public void setReleaseAt(String releaseAt) {
        if(releaseAt != null)
            this.releaseAt = LocalDateTime.parse(releaseAt);
    }

    public void setLeaseInfo(Map info) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.leaseInfo = objectMapper.convertValue(info, LeaseInfoDto.class);
    }
    public void setLeasePrice(Map price) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.leasePrice = objectMapper.convertValue(price, LeasePriceDto.class);
    }
}
