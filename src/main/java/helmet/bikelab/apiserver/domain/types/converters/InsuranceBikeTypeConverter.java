package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InsuranceBikeTypes;

import javax.persistence.AttributeConverter;

public class InsuranceBikeTypeConverter implements AttributeConverter<InsuranceBikeTypes, String> {
    @Override
    public String convertToDatabaseColumn(InsuranceBikeTypes attribute) {
        return attribute == null ? null : attribute.getType();
    }

    @Override
    public InsuranceBikeTypes convertToEntityAttribute(String dbData) {
        return InsuranceBikeTypes.getType(dbData);
    }
}
