package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsTypeByIdRequest extends OriginObject {
    private Integer partsTypeNo;
}
