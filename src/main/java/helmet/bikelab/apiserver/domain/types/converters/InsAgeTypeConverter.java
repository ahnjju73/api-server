package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InsAgeTypes;

import javax.persistence.AttributeConverter;

public class InsAgeTypeConverter implements AttributeConverter<InsAgeTypes, String> {
    @Override
    public String convertToDatabaseColumn(InsAgeTypes attribute) {
        return attribute == null ? null : attribute.getAge();
    }

    @Override
    public InsAgeTypes convertToEntityAttribute(String dbData) {
        return InsAgeTypes.getAge(dbData);
    }
}
