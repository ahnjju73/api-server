package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.RiderLeaseRequestedTypes;

import javax.persistence.AttributeConverter;

public class RiderLeaseRequestedTypesConverter implements AttributeConverter<RiderLeaseRequestedTypes, String> {
    @Override
    public String convertToDatabaseColumn(RiderLeaseRequestedTypes attribute) {
        if(attribute == null) return RiderLeaseRequestedTypes.NOT.getLeaseRequested();
        return attribute.getLeaseRequested();
    }

    @Override
    public RiderLeaseRequestedTypes convertToEntityAttribute(String dbData) {
        return RiderLeaseRequestedTypes.getRiderLeaseRequestedTypes(dbData);
    }
}
