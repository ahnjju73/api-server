package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ManagementTypes;

import javax.persistence.AttributeConverter;

public class ManagementTypeConverter implements AttributeConverter<ManagementTypes, String> {

    @Override
    public String convertToDatabaseColumn(ManagementTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public ManagementTypes convertToEntityAttribute(String dbData) {
        return ManagementTypes.getManagementStatus(dbData);
    }
}
