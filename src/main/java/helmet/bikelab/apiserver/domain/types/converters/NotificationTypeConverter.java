package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.NotificationTypes;

import javax.persistence.AttributeConverter;

public class NotificationTypeConverter implements AttributeConverter<NotificationTypes, String> {
    @Override
    public String convertToDatabaseColumn(NotificationTypes attribute) {
        return attribute == null ? null : attribute.getType();
    }

    @Override
    public NotificationTypes convertToEntityAttribute(String dbData) {
        return NotificationTypes.getType(dbData);
    }
}
