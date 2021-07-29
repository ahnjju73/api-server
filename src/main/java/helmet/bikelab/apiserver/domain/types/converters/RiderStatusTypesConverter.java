package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.RiderStatusTypes;

import javax.persistence.AttributeConverter;

public class RiderStatusTypesConverter implements AttributeConverter<RiderStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(RiderStatusTypes status) {
        return status.getRiderStatusType();
    }

    @Override
    public RiderStatusTypes convertToEntityAttribute(String status) {
        return RiderStatusTypes.getRiderStatusTypes(status);
    }
}
