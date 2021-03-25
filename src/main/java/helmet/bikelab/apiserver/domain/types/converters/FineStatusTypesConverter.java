package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.FineStatusTypes;

import javax.persistence.AttributeConverter;

public class FineStatusTypesConverter implements AttributeConverter<FineStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(FineStatusTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public FineStatusTypes convertToEntityAttribute(String dbData) {
        return FineStatusTypes.getFineStatus(dbData);
    }
}
