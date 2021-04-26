package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;

import javax.persistence.AttributeConverter;

public class LeaseStatusTypesConverter implements AttributeConverter<LeaseStatusTypes, String> {
    @Override
    public String convertToDatabaseColumn(LeaseStatusTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public LeaseStatusTypes convertToEntityAttribute(String dbData) {
        return LeaseStatusTypes.getLeaseStatus(dbData);
    }
}
