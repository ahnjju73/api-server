package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.CustomEstimates;
import helmet.bikelab.apiserver.domain.CustomProvisionalEstimates;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchCustomEstimatesResponse {
    private Long customEstimateNo;
    private String customEstimateId;
    private String bikeNum;
    private String bikeModelCode;
    private String insuranceNumber;
    private CommonBikes bikeModel;
    private Integer rateAccident;
    private List<CustomProvisionalEstimates> provisionalEstimates;
    private LocalDateTime createdAt;
    private Integer workingPrice;

    private String deptName;
    private String deptCenter;

    public void setCustomEstimate(CustomEstimates customEstimates){
        this.customEstimateNo = customEstimates.getCustomEstimateNo();
        this.customEstimateId = customEstimates.getCustomEstimateId();
        this.bikeModel = customEstimates.getBikeModel();
        this.bikeNum = customEstimates.getBikeNum();
        this.bikeModelCode = customEstimates.getBikeModelCode();
        this.rateAccident = customEstimates.getRateAccident();
        this.provisionalEstimates = customEstimates.getProvisionalEstimates();
        this.createdAt = customEstimates.getCreatedAt();
        this.insuranceNumber = customEstimates.getInsuranceNumber();
    }

}
