package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChangeInspectionDateRequest {
    private String inspectId;
    private LocalDateTime changeDt;

    public void setChangeDt(String changeDt){
        try {
            this.changeDt = LocalDateTime.parse(changeDt);
        }catch (Exception e){
            this.changeDt = LocalDateTime.parse(changeDt + "T12:00:00");
        }
    }

}
