package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SessionResponseDto {
    private String name;
    private String email;
    private String thumbnail;
    private BikeUserStatusTypes status;
    private String statusCode;
    private String userId;
    private String sessionKey;
}
