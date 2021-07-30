package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeRiderStatusTypes;

import javax.persistence.AttributeConverter;

public class BikeRiderStatusTypesConverter implements AttributeConverter<BikeRiderStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(BikeRiderStatusTypes status) {
        if(status == null)
            return null;
        return status.getRiderStatus();
    }

    @Override
    public BikeRiderStatusTypes convertToEntityAttribute(String status) {
        return BikeRiderStatusTypes.getBikeRiderStatusTypes(status);
    }
}
