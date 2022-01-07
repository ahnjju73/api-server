package helmet.bikelab.apiserver.domain.bike;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.domain.types.converters.MediaTypesConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
