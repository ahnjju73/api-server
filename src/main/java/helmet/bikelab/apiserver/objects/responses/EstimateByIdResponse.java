package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.objects.EstimateDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EstimateByIdResponse {
    private Estimates estimate;
    private List<Map> parts = new ArrayList<>();
    private List<Map> attachments = new ArrayList<>();
    private Leases lease;
    private Integer workingPrice;
}