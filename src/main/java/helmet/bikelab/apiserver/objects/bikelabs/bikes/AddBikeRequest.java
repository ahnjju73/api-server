package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddBikeRequest extends OriginObject {
    private String vimNum;
    private String number;
    private String carModel;
    private String color;
    private LocalDateTime receiveDt;

    public void setReceiveDt(String receiveDt) {
        this.receiveDt = LocalDateTime.parse(receiveDt);
    }

}
