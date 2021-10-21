package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.RiderVerifiedRequestTypes;

import javax.persistence.AttributeConverter;

public class RiderVerifiedRequestTypesConverter implements AttributeConverter<RiderVerifiedRequestTypes, String> {

    @Override
    public String convertToDatabaseColumn(RiderVerifiedRequestTypes attribute) {
        return attribute.getVerifiedRequestType();
    }

    @Override
    public RiderVerifiedRequestTypes convertToEntityAttribute(String dbData) {
        return RiderVerifiedRequestTypes.getRiderVerifiedRequestTypes(dbData);
    }

}
