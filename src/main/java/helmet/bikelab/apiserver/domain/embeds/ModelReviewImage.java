package helmet.bikelab.apiserver.domain.embeds;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.domain.types.converters.MediaTypesConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.util.UUID;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Embeddable
public class ModelReviewImage {

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