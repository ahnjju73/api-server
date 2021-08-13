package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeTypes;

import javax.persistence.AttributeConverter;

public class BikeTypesConverter implements AttributeConverter<BikeTypes, String> {
    @Override
    public String convertToDatabaseColumn(BikeTypes attribute) {
        return attribute.getType();
    }

    @Override
    public BikeTypes convertToEntityAttribute(String dbData) {
        return BikeTypes.getType(dbData);
    }
}
