package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InsCompanyTypes;

import javax.persistence.AttributeConverter;

public class InsCompanyTypeConverter implements AttributeConverter<InsCompanyTypes, String> {
    @Override
    public String convertToDatabaseColumn(InsCompanyTypes attribute) {
        return attribute != null ? attribute.getType() : null;
    }

    @Override
    public InsCompanyTypes convertToEntityAttribute(String dbData) {
        return InsCompanyTypes.getCompanyType(dbData);
    }
}
