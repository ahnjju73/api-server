package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeAttachmentTypes;
import helmet.bikelab.apiserver.objects.requests.BikeByIdRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateBikeAttachmentTypeRequest extends BikeByIdRequest {
    private Integer bikeFileInfoNo;
    private BikeAttachmentTypes attachmentType;

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = BikeAttachmentTypes.getBikeAttachmentTypes(attachmentType);
    }
}
