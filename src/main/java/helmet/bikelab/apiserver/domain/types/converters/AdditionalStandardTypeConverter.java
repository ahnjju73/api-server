package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.AdditionalStandardTypes;

import javax.persistence.AttributeConverter;

public class AdditionalStandardTypeConverter implements AttributeConverter<AdditionalStandardTypes, String> {

    @Override
    public String convertToDatabaseColumn(AdditionalStandardTypes attribute) {
        return attribute != null ? attribute.getPrice() : null;
    }

    @Override
    public AdditionalStandardTypes convertToEntityAttribute(String dbData) {
        return AdditionalStandardTypes.getType(dbData);
    }
}
