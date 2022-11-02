package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.RiderInsuranceStatus;

import javax.persistence.AttributeConverter;

public class RiderInsuranceStatusConverter implements AttributeConverter<RiderInsuranceStatus, String> {
    @Override
    public String convertToDatabaseColumn(RiderInsuranceStatus attribute) {
        return attribute == null ? null : attribute.getStatus();
    }

    @Override
    public RiderInsuranceStatus convertToEntityAttribute(String dbData) {
        return RiderInsuranceStatus.getStatus(dbData);
    }
}
