package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateRegularInspectionRequest {
    private String inspectId;
    private String clientId;
    private String shopId;
    private List<PresignedURLVo> newAttachments;
    private List<ModelAttachment> attachments;
    private String order;
    private LocalDateTime inspectDt;
    private String includeDt;

}
