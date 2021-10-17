package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.RiderVerifiedTypes;

import javax.persistence.AttributeConverter;

public class RiderVerifiedTypesConverter implements AttributeConverter<RiderVerifiedTypes, String> {

    @Override
    public String convertToDatabaseColumn(RiderVerifiedTypes attribute) {
        return attribute.getVerifiedType();
    }

    @Override
    public RiderVerifiedTypes convertToEntityAttribute(String dbData) {
        return RiderVerifiedTypes.getRiderVerifiedTypes(dbData);
    }

}
