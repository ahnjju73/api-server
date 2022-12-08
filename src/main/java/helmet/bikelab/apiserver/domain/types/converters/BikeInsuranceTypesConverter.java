package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeInsuranceTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceTypes;

import javax.persistence.AttributeConverter;

public class BikeInsuranceTypesConverter implements AttributeConverter<BikeInsuranceTypes, String> {
    @Override
    public String convertToDatabaseColumn(BikeInsuranceTypes attribute) {
        return attribute != null ? attribute.getType() : null;
    }

    @Override
    public BikeInsuranceTypes convertToEntityAttribute(String dbData) {
        return BikeInsuranceTypes.getBikeInsuranceTypes(dbData);
    }
}
