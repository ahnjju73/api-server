package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsCodeListRequest extends PageableRequest {
    private String partsName;
    private String partsType;

    public String getPartsName() {
        return partsName == null ? "" : partsName;
    }

    public String getPartsType() {
        return partsType == null ? "" : partsType;
    }
}
