package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InquiryStatusTypes;

import javax.persistence.AttributeConverter;

public class InquiryStatusTypesConverter implements AttributeConverter<InquiryStatusTypes, String> {
    @Override
    public String convertToDatabaseColumn(InquiryStatusTypes inquiryStatusTypes) {
        return inquiryStatusTypes.getStatus();
    }

    @Override
    public InquiryStatusTypes convertToEntityAttribute(String inquiryStatusTypes) {
        return InquiryStatusTypes.getInquiryStatusTypes(inquiryStatusTypes);
    }
}
