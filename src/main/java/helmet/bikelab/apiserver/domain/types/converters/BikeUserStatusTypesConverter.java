package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;

import javax.persistence.AttributeConverter;

public class BikeUserStatusTypesConverter implements AttributeConverter<BikeUserStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(BikeUserStatusTypes userStatusTypes) {
        return userStatusTypes.getStatus();
    }

    @Override
    public BikeUserStatusTypes convertToEntityAttribute(String userStatusTypes) {
        return BikeUserStatusTypes.getBikeUserStatus(userStatusTypes);
    }

}
