package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.EstimateStatusTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EstimateRequestListDto extends RequestListDto{
    private String keyword;
    private EstimateStatusTypes status;
    private String groupId;
    private String clientId;
    private String riderId;
    private String shopId;
    private String estimateId;
    private String bikeNumber;
    private String startAt;
    private String endAt;
    private String limited;
    private String isDeleted;
    private String accident;
    private String partName;

    public void setStatus(String status) {
        this.status = EstimateStatusTypes.getEstimateStatusTypes(status);
    }

}
