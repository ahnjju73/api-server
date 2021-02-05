package helmet.bikelab.apiserver.objects;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SessionResponseDto {
    private String name;
    private String email;
    private String thumbnail;
    private String status;
    private String userId;
    private String sessionKey;
}
