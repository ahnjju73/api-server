package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeStatusTypes;
import helmet.bikelab.apiserver.domain.types.BikeTypes;

import javax.persistence.AttributeConverter;

public class BikeStatusTypesConverter implements AttributeConverter<BikeStatusTypes, String> {
    @Override
    public String convertToDatabaseColumn(BikeStatusTypes attribute) {
        return attribute.getType();
    }

    @Override
    public BikeStatusTypes convertToEntityAttribute(String dbData) {
        return BikeStatusTypes.getBikeStatusTypes(dbData);
    }
}
