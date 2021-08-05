package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.DemandLeaseStatusTypes;

import javax.persistence.AttributeConverter;

public class DemandLeaseStatusTypesConverter implements AttributeConverter<DemandLeaseStatusTypes, String> {
    @Override
    public String convertToDatabaseColumn(DemandLeaseStatusTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public DemandLeaseStatusTypes convertToEntityAttribute(String dbData) {
        return DemandLeaseStatusTypes.getDemandLeaseStatusTypes(dbData);
    }
}
