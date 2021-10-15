package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.PaidTypes;

import javax.persistence.AttributeConverter;

public class PaidTypeConverter implements AttributeConverter<PaidTypes, String> {
    @Override
    public String convertToDatabaseColumn(PaidTypes attribute) {
        return attribute == null ? null : attribute.getStatus();
    }

    @Override
    public PaidTypes convertToEntityAttribute(String dbData) {
        return PaidTypes.getStatus(dbData);
    }
}
