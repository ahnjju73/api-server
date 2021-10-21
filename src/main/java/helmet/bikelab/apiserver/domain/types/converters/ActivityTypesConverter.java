package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ActivityTypes;

import javax.persistence.AttributeConverter;

public class ActivityTypesConverter implements AttributeConverter<ActivityTypes, String> {
    @Override
    public String convertToDatabaseColumn(ActivityTypes attribute) {
        return attribute.getActivityType();
    }

    @Override
    public ActivityTypes convertToEntityAttribute(String dbData) {
        return ActivityTypes.getRiderActivityTypes(dbData);
    }
}
