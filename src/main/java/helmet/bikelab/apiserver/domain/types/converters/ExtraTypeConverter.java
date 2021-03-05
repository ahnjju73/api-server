package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ExtraTypes;

import javax.persistence.AttributeConverter;

public class ExtraTypeConverter implements AttributeConverter<ExtraTypes, String> {
    @Override
    public String convertToDatabaseColumn(ExtraTypes attribute) {
        return attribute.getExtra();
    }

    @Override
    public ExtraTypes convertToEntityAttribute(String dbData) {
        return ExtraTypes.getExtraType(dbData);
    }
}
