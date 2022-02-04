package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.SettleStatusTypes;

import javax.persistence.AttributeConverter;

public class SettleStatusTypeConverter implements AttributeConverter<SettleStatusTypes, String> {
    @Override
    public String convertToDatabaseColumn(SettleStatusTypes attribute) {
        return attribute != null? attribute.getStatus() : null;
    }

    @Override
    public SettleStatusTypes convertToEntityAttribute(String dbData) {
        return SettleStatusTypes.getStatusType(dbData);
    }
}
