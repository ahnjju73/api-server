package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SectionAxisRequest extends SectionsByIdRequest {
    private String name;
    private Map axis;

    public void checkValidation(){
        if(!bePresent(name)) withException("506-010");
        if (!bePresent(axis)) withException("506-011");
    }
}
