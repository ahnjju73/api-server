package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.Chasoo;
import helmet.bikelab.apiserver.domain.types.RoleTypes;

import javax.persistence.AttributeConverter;

public class RoleTypeConverter implements AttributeConverter<RoleTypes, String> {
    @Override
    public String convertToDatabaseColumn(RoleTypes attribute) {
        return attribute != null ? attribute.getType() : null;
    }

    @Override
    public RoleTypes convertToEntityAttribute(String dbData) {
        return RoleTypes.getType(dbData);
    }
}
