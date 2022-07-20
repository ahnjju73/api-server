package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.Sections;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchSections {
    private Sections sections;
    private Integer partsCnt;
}
