package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.PaymentTypes;

import javax.persistence.AttributeConverter;

public class PaymentTypeConverter implements AttributeConverter<PaymentTypes, String> {
    @Override
    public String convertToDatabaseColumn(PaymentTypes attribute) {
        return attribute.getPaymentType();
    }

    @Override
    public PaymentTypes convertToEntityAttribute(String dbData) {
        return PaymentTypes.getPaymentType(dbData);
    }
}
