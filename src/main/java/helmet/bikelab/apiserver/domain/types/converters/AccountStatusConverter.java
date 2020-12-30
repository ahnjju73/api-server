package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;

import javax.persistence.AttributeConverter;

public class AccountStatusConverter implements AttributeConverter<AccountStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(AccountStatusTypes attribute) {
        return attribute.getAccountStatus();
    }

    @Override
    public AccountStatusTypes convertToEntityAttribute(String dbData) {
        return AccountStatusTypes.getAccountStatus(dbData);
    }

}
