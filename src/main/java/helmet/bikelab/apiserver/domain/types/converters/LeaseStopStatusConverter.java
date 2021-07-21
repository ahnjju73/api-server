package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;

import javax.persistence.AttributeConverter;

public class LeaseStopStatusConverter implements AttributeConverter<LeaseStopStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(LeaseStopStatusTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public LeaseStopStatusTypes convertToEntityAttribute(String dbData) {
        return LeaseStopStatusTypes.getLeaseStopStatus(dbData);
    }
}
