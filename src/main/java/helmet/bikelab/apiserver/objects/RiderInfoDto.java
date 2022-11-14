package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RiderInfoDto {
    private String riderId;
    private String riderStatus;
    private String riderEmail;
    private String riderPhone;
    private String riderName;
    private String riderSsn;
}
