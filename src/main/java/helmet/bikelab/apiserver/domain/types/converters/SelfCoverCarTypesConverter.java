package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.AccountTypes;
import helmet.bikelab.apiserver.domain.types.SelfCoverCarTypes;

import javax.persistence.AttributeConverter;

public class SelfCoverCarTypesConverter implements AttributeConverter<SelfCoverCarTypes, String> {

    @Override
    public String convertToDatabaseColumn(SelfCoverCarTypes attribute) {
        return attribute.getCoverType();
    }

    @Override
    public SelfCoverCarTypes convertToEntityAttribute(String dbData) {
        return SelfCoverCarTypes.getSelfCoverCarTypes(dbData);
    }
}
