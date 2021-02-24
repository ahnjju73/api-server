package helmet.bikelab.apiserver.objects.bikelabs.systems;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HandlePermissionRequest {
    private Integer userNo;
    private String pgmId;
}
