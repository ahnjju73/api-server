package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ExpireTypes;
import helmet.bikelab.apiserver.domain.types.ReadWriteTypes;

import javax.persistence.AttributeConverter;

public class ExpireTypesConverter implements AttributeConverter<ExpireTypes, String> {

    @Override
    public String convertToDatabaseColumn(ExpireTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public ExpireTypes convertToEntityAttribute(String dbData) {
        return ExpireTypes.getExpireTypes(dbData);
    }

}
