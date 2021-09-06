package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.domain.types.converters.MediaTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.PartsBackUpConverter;
import helmet.bikelab.apiserver.domain.types.converters.UnitTypesConverter;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PartsImages {

    private String id = UUID.randomUUID().toString().replaceAll("-", "");
    @Convert(converter = MediaTypesConverter.class)
    private MediaTypes mediaType;
    private String mediaTypeCode;
    private String uri;
    private String domain;
    private String fileName;

    public void setMediaType(String mediaType) {
        this.mediaType = MediaTypes.getMediaTypes(mediaType);
        this.mediaTypeCode = this.mediaType.getStatus();
    }

}
