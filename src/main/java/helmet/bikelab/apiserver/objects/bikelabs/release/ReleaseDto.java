package helmet.bikelab.apiserver.objects.bikelabs.release;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReleaseDto {
    private String releaseName;
    private String useYn;
    private String createdAt;
    private ModelAddress releaseAddress;
}
