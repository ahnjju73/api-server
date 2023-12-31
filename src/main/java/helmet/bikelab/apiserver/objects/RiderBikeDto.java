package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderBikeDto {
    private Integer bikeNo;
    private String bikeId;
    private String bikeNum;
    private String vimNum;
    private String bikeModel;
    private double bikeVolume;
    private String bikeType;
    private LocalDateTime riderStartAt;
    private LocalDateTime riderEndAt;
}
