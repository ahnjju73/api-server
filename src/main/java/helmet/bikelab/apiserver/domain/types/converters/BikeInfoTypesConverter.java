package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeAttachmentTypes;
import helmet.bikelab.apiserver.domain.types.BikeInfoTypes;

import javax.persistence.AttributeConverter;

public class BikeInfoTypesConverter implements AttributeConverter<BikeInfoTypes, String> {
    @Override
    public String convertToDatabaseColumn(BikeInfoTypes attribute) {
        return attribute == null ? null : attribute.getInfoType();
    }

    @Override
    public BikeInfoTypes convertToEntityAttribute(String dbData) {
        return BikeInfoTypes.getBikeInfoTypes(dbData);
    }
}
