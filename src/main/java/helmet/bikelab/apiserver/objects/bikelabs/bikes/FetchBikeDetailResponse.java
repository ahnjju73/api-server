package helmet.bikelab.apiserver.objects.bikelabs.bikes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchBikeDetailResponse extends OriginObject {
    private String clientName;
    private String vimNum;
    private String carNum;
    private String carModel;
    private String color;
    private LocalDateTime receiveDt;
    private LocalDateTime registerDt;
}
