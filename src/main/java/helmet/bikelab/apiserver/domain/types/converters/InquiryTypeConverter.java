package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InquiryType;

import javax.persistence.AttributeConverter;

public class InquiryTypeConverter implements AttributeConverter<InquiryType, String> {
    @Override
    public String convertToDatabaseColumn(InquiryType attribute) {
        return attribute != null ? attribute.getType() : null;
    }

    @Override
    public InquiryType convertToEntityAttribute(String dbData) {
        return InquiryType.getType(dbData);
    }
}
