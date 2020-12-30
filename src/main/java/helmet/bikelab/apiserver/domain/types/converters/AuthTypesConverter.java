package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.AuthTypes;

import javax.persistence.AttributeConverter;

public class AuthTypesConverter implements AttributeConverter<AuthTypes, String> {

    @Override
    public String convertToDatabaseColumn(AuthTypes attribute) {
        return attribute.getAuth();
    }

    @Override
    public AuthTypes convertToEntityAttribute(String dbData) {
        return AuthTypes.getAuth(dbData);
    }

}
