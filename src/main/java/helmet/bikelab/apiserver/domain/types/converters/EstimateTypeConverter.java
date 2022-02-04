package helmet.bikelab.apiserver.domain.types.converters;


import helmet.bikelab.apiserver.domain.types.EstimateTypes;

import javax.persistence.AttributeConverter;

public class EstimateTypeConverter implements AttributeConverter<EstimateTypes, String> {
    @Override
    public String convertToDatabaseColumn(EstimateTypes attribute) {
        return attribute.getType();
    }

    @Override
    public EstimateTypes convertToEntityAttribute(String dbData) {
        return EstimateTypes.getType(dbData);
    }
}
