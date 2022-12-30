package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsCodeByIdRequest extends OriginObject {
    private Integer partsCodeNo;
}
