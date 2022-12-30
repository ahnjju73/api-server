package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.UsageTypes;

import javax.persistence.AttributeConverter;

public class UsageTypeConverter implements AttributeConverter<UsageTypes, String> {

    @Override
    public String convertToDatabaseColumn(UsageTypes attribute) {
        return attribute == null ? null : attribute.getType();
    }

    @Override
    public UsageTypes convertToEntityAttribute(String dbData) {
        return UsageTypes.getType(dbData);
    }
}
