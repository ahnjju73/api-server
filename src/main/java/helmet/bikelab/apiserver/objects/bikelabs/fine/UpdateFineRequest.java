package helmet.bikelab.apiserver.objects.bikelabs.fine;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateFineRequest extends OriginObject {
    private String fineNum;
    private String bikeId;
    private LocalDateTime fineDate;
    private Integer fee;

    public void setFineDate(String fineDate){
        this.fineDate =  LocalDateTime.parse(fineDate);
    }

    public void checkValidation(){
        if(!bePresent(fineNum)) withException("");
        if(!bePresent(bikeId)) withException("");
    }

}
