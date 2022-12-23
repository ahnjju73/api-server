package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchRegularInspectionRequest extends PageableRequest {
    private String inspectId;
    private String clientId;
    private String groupId;
    private String shopId;
    private LocalDateTime startDt;
    private LocalDateTime endDt;

    public void setStartDt(String startDt){
        try {
            this.startDt = LocalDateTime.parse(startDt);
        }catch (Exception e){
            this.startDt = LocalDateTime.parse(startDt + "T00:00:00");
        }
    }

    public void setEndDt(String endDt){
        try {
            this.endDt = LocalDateTime.parse(endDt);
        }catch (Exception e){
            this.endDt = LocalDateTime.parse(endDt + "T23:59:59");
        }
    }
}
