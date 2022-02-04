package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.EstimateStatusTypes;

import javax.persistence.AttributeConverter;

public class EstimateStatusTypesConverter implements AttributeConverter<EstimateStatusTypes, String> {

    @Override
    public String convertToDatabaseColumn(EstimateStatusTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public EstimateStatusTypes convertToEntityAttribute(String dbData) {
        return EstimateStatusTypes.getEstimateStatusTypes(dbData);
    }

}
