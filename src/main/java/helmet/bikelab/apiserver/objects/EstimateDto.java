package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikePartsDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EstimateDto {

    private String estimateId;
    private LocalDateTime createdAt;
    private String status;
    private String description;
    private BikeDto bike;
    private RiderInfoDto rider;
    private List<BikePartsDto> parts;
    private List<RiderDemandLeaseAttachmentDto> attachments;

}
