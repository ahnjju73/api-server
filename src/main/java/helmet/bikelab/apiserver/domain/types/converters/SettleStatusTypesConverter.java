package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.SettleStatusTypes;

import javax.persistence.AttributeConverter;

public class SettleStatusTypesConverter implements AttributeConverter<SettleStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(SettleStatusTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public SettleStatusTypes convertToEntityAttribute(String dbData) {
        return SettleStatusTypes.getSettleStatusTypes(dbData);
    }

}
