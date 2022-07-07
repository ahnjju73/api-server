package helmet.bikelab.apiserver.objects.responses;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.SectionAxis;
import helmet.bikelab.apiserver.domain.SectionAxisParts;
import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SectionDetailResponse extends OriginObject {
    private Sections section;
    private List<SectionAxis> sectionAxisList;
}
