package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.PayerTypes;

import javax.persistence.AttributeConverter;

public class PayerTypesConverter implements AttributeConverter<PayerTypes, String> {

    @Override
    public String convertToDatabaseColumn(PayerTypes attribute) {
        return attribute.getStatus();
    }

    @Override
    public PayerTypes convertToEntityAttribute(String dbData) {
        return PayerTypes.getPayerTypes(dbData);
    }
}
