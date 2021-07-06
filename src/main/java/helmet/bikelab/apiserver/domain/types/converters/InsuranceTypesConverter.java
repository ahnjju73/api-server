package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InsuranceTypes;

import javax.persistence.AttributeConverter;

public class InsuranceTypesConverter implements AttributeConverter<InsuranceTypes, String> {
    @Override
    public String convertToDatabaseColumn(InsuranceTypes attribute) {
        return attribute.getType();
    }

    @Override
    public InsuranceTypes convertToEntityAttribute(String dbData) {
        return InsuranceTypes.getInsuranceType(dbData);
    }
}
