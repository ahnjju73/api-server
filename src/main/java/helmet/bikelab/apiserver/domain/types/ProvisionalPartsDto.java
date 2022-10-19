package helmet.bikelab.apiserver.domain.types;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.objects.ProvisionalParts;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProvisionalPartsDto extends ProvisionalParts {
    private List<ImageVo> images;
}
