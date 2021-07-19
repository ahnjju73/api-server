package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.types.converters.MediaTypesConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class ModelMedia {

    @Column(name = "media_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = MediaTypesConverter.class)
    private MediaTypes mediaType = MediaTypes.IMAGE;

    @Column(name = "domain", nullable = false)
    private String domain;

    @Column(name = "uri", length = 1025, nullable = false)
    private String uri;

}
