package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.YesNoTypes;

import javax.persistence.AttributeConverter;

public class BikeUserStatusTypesConverter implements AttributeConverter<YesNoTypes, String> {

    @Override
    public String convertToDatabaseColumn(YesNoTypes yesNoType) {
        return yesNoType.getYesNo();
    }

    @Override
    public YesNoTypes convertToEntityAttribute(String yesNo) {
        return YesNoTypes.getYesNo(yesNo);
    }

}
