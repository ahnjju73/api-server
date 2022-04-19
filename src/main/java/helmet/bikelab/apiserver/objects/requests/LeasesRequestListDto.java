package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.ContractTypes;
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

    public void setContractType(String contractType) {
        this.contractType = ContractTypes.getContractType(contractType);
        if(bePresent(this.contractType)) this.contractTypeCode = this.contractType.getStatus();
    }

}
