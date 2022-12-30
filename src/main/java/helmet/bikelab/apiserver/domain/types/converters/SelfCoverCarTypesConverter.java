package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.AccountTypes;
import helmet.bikelab.apiserver.domain.types.SelfCoverCarTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;

import javax.persistence.AttributeConverter;

public class SelfCoverCarTypesConverter extends OriginObject implements AttributeConverter<SelfCoverCarTypes, String> {

    @Override
    public String convertToDatabaseColumn(SelfCoverCarTypes attribute) {
        return !bePresent(attribute) ? null : attribute.getCoverType();
    }

    @Override
    public SelfCoverCarTypes convertToEntityAttribute(String dbData) {
        return SelfCoverCarTypes.getSelfCoverCarTypes(dbData);
    }
}
