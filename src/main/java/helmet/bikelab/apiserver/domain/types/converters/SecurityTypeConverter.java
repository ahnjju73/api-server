package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.SecurityTypes;

import javax.persistence.AttributeConverter;

public class SecurityTypeConverter implements AttributeConverter<SecurityTypes, String> {

    @Override
    public String convertToDatabaseColumn(SecurityTypes attribute) {
        return attribute.getSecurity();
    }

    @Override
    public SecurityTypes convertToEntityAttribute(String dbData) {
        return SecurityTypes.getSecurityType(dbData);
    }
}
