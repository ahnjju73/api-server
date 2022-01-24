package helmet.bikelab.apiserver.domain.types.converters;


import helmet.bikelab.apiserver.domain.types.EstimateHistoryTypes;

import javax.persistence.AttributeConverter;


public class EstimateHistoryTypesConverter implements AttributeConverter<EstimateHistoryTypes, String> {

    @Override
    public String convertToDatabaseColumn(EstimateHistoryTypes status) {
        return status.getHistoryType();
    }

    @Override
    public EstimateHistoryTypes convertToEntityAttribute(String status) {
        return EstimateHistoryTypes.getEstimateHistoryTypes(status);
    }
}
