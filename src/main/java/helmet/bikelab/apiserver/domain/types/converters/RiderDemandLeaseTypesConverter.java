package helmet.bikelab.apiserver.domain.types.converters;



import helmet.bikelab.apiserver.domain.types.RiderDemandLeaseTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;

import javax.persistence.AttributeConverter;

public class RiderDemandLeaseTypesConverter extends OriginObject implements AttributeConverter<RiderDemandLeaseTypes, String> {

    @Override
    public String convertToDatabaseColumn(RiderDemandLeaseTypes attribute) {
        return bePresent(attribute) ? attribute.getLeaseType() : null;
    }

    @Override
    public RiderDemandLeaseTypes convertToEntityAttribute(String dbData) {
        return RiderDemandLeaseTypes.getRiderDemandLeaseTypes(dbData);
    }

}
