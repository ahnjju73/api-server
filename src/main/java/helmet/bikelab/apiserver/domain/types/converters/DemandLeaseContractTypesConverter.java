package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.DemandLeaseContractTypes;

import javax.persistence.AttributeConverter;

public class DemandLeaseContractTypesConverter implements AttributeConverter<DemandLeaseContractTypes, String> {
    @Override
    public String convertToDatabaseColumn(DemandLeaseContractTypes attribute) {
        return attribute.getContractType();
    }

    @Override
    public DemandLeaseContractTypes convertToEntityAttribute(String dbData) {
        return DemandLeaseContractTypes.getDemandLeaseContractTypes(dbData);
    }
}
