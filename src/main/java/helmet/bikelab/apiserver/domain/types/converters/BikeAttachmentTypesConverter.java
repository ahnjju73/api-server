package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ActivityTypes;
import helmet.bikelab.apiserver.domain.types.BikeAttachmentTypes;

import javax.persistence.AttributeConverter;

public class BikeAttachmentTypesConverter implements AttributeConverter<BikeAttachmentTypes, String> {
    @Override
    public String convertToDatabaseColumn(BikeAttachmentTypes attribute) {
        return attribute == null ? null : attribute.getAttachmentType();
    }

    @Override
    public BikeAttachmentTypes convertToEntityAttribute(String dbData) {
        return BikeAttachmentTypes.getBikeAttachmentTypes(dbData);
    }
}
