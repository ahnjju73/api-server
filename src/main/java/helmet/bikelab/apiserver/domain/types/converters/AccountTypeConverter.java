package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.AccountTypes;

import javax.persistence.AttributeConverter;

public class AccountTypeConverter implements AttributeConverter<AccountTypes, String> {

    @Override
    public String convertToDatabaseColumn(AccountTypes attribute) {
        return attribute.getType();
    }

    @Override
    public AccountTypes convertToEntityAttribute(String dbData) {
        return AccountTypes.getType(dbData);
    }
}
