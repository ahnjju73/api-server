package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BusinessTypes;

import javax.persistence.AttributeConverter;

public class BusinessTypeTaxConverter implements AttributeConverter<Double, String> {

    @Override
    public String convertToDatabaseColumn(Double attribute) {
        return null;
    }

    @Override
    public Double convertToEntityAttribute(String dbData) {
        return BusinessTypes.getBusinessTypes(dbData) == null ? 0.0 : BusinessTypes.getBusinessTypes(dbData).getTaxRate();
    }
}
