package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ContractTypes;

import javax.persistence.AttributeConverter;

public class ContractTypeConverter implements AttributeConverter<ContractTypes, String> {

    @Override
    public String convertToDatabaseColumn(ContractTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public ContractTypes convertToEntityAttribute(String dbData) {
        return ContractTypes.getContractType(dbData);
    }
}
