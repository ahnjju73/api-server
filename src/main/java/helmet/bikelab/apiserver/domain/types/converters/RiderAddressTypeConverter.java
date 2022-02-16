package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.RiderAddressTypes;

import javax.persistence.AttributeConverter;

public class RiderAddressTypeConverter implements AttributeConverter<RiderAddressTypes, String> {
    @Override
    public String convertToDatabaseColumn(RiderAddressTypes attribute) {
        return attribute == null ? null : attribute.getType();
    }

    @Override
    public RiderAddressTypes convertToEntityAttribute(String dbData) {
        return RiderAddressTypes.getType(dbData);
    }
}
