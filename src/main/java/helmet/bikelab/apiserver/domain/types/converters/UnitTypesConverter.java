package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.PayerTypes;
import helmet.bikelab.apiserver.domain.types.UnitTypes;

import javax.persistence.AttributeConverter;

public class UnitTypesConverter implements AttributeConverter<UnitTypes, String> {

    @Override
    public String convertToDatabaseColumn(UnitTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public UnitTypes convertToEntityAttribute(String dbData) {
        return UnitTypes.getUnitTypes(dbData);
    }
}
