package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.MediaTypes;

import javax.persistence.AttributeConverter;


public class MediaTypesConverter implements AttributeConverter<MediaTypes, String> {

    @Override
    public String convertToDatabaseColumn(MediaTypes status) {
        return status.getStatus();
    }

    @Override
    public MediaTypes convertToEntityAttribute(String status) {
        return MediaTypes.getMediaTypes(status);
    }
}
