package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InsuranceCompanyStatusTypes;

import javax.persistence.AttributeConverter;

public class InsuranceStatusTypesConverter implements AttributeConverter<InsuranceCompanyStatusTypes, String> {
    @Override
    public String convertToDatabaseColumn(InsuranceCompanyStatusTypes attribute) {
        return attribute != null ? attribute.getStatus() : null;
    }

    @Override
    public InsuranceCompanyStatusTypes convertToEntityAttribute(String dbData) {
        return InsuranceCompanyStatusTypes.getInsuranceStatus(dbData);
    }
}
