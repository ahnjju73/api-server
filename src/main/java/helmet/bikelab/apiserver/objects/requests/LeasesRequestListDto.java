package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LeasesRequestListDto extends RequestListDto{

    private String clientId;
    private Integer searchClientNo;
    private String searchBike;
    private String leaseId;
    private ContractTypes contractType;
    private String contractTypeCode;
    private LeaseStopStatusTypes leaseStopStatus;
    private String leaseStopStatusCode;

    public void setLeaseStopStatus(String leaseStopStatus) {
        try {
            if(bePresent(leaseStopStatus)) {
                this.leaseStopStatus = LeaseStopStatusTypes.getLeaseStopStatus(leaseStopStatus);
                this.leaseStopStatusCode = this.leaseStopStatus.getStatus();
            }
        }catch (Exception e){

        }
    }

    public void setContractType(String contractType) {
        this.contractType = ContractTypes.getContractType(contractType);
        if(bePresent(this.contractType)) this.contractTypeCode = this.contractType.getStatus();
    }

}
