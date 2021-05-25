package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;

import javax.persistence.AttributeConverter;

public class BikeUserLogTypesConverter implements AttributeConverter<BikeUserLogTypes, String> {
    @Override
    public String convertToDatabaseColumn(BikeUserLogTypes bikeUserLogTypes) {
        return bikeUserLogTypes.getStatus();
    }

    @Override
    public BikeUserLogTypes convertToEntityAttribute(String bikeUserLogTypes) {
        return BikeUserLogTypes.getBikeUserStatus(bikeUserLogTypes);
    }
}
