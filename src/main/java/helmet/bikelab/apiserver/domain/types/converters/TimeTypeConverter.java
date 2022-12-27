package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.TimeTypes;

import javax.persistence.AttributeConverter;

public class TimeTypeConverter implements AttributeConverter<TimeTypes, String> {

    @Override
    public String convertToDatabaseColumn(TimeTypes timeTypes) {

        return timeTypes == null ? null : timeTypes.getTime();
    }

    @Override
    public TimeTypes convertToEntityAttribute(String timeTypes) {
        return TimeTypes.getType(timeTypes);
    }

}
