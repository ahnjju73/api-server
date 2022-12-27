package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
    private List<PresignedURLVo> attachments;
    private String order;
    private LocalDateTime inspectDt;
    private LocalDateTime includeDt;

    public void setInspectDt(String startDt){
        try {
            this.inspectDt = LocalDateTime.parse(startDt);
        }catch (Exception e){
            this.inspectDt = LocalDateTime.parse(startDt + "T12:00:00");
        }
    }

    public void setIncludeDt(String endDt){
        try {
            this.includeDt = LocalDateTime.parse(endDt);
        }catch (Exception e){
            this.includeDt = LocalDateTime.parse(endDt + "T00:00:00");
        }
    }
}
