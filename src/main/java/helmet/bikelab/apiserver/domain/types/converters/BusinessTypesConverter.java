package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;

import javax.persistence.AttributeConverter;

public class BusinessTypesConverter implements AttributeConverter<BusinessTypes, String> {
    @Override
    public String convertToDatabaseColumn(BusinessTypes businessTypes) {
        return businessTypes.getBusinessType();
    }

    @Override
    public BusinessTypes convertToEntityAttribute(String businessTypes) {
        return BusinessTypes.getBusinessTypes(businessTypes);
    }
}
