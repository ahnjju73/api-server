package helmet.bikelab.apiserver.domain.types;

import javax.persistence.AttributeConverter;

public class YesNoTypeConverter implements AttributeConverter<YesNoTypes, String> {

    @Override
    public String convertToDatabaseColumn(YesNoTypes yesNoType) {
        return yesNoType.getYesNo();
    }

    @Override
    public YesNoTypes convertToEntityAttribute(String yesNo) {
        return YesNoTypes.getYesNo(yesNo);
    }

}
