package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.InsRangeTypes;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;

import javax.persistence.AttributeConverter;

public class InsRangeTypeConverter implements AttributeConverter<InsRangeTypes, String> {
    @Override
    public String convertToDatabaseColumn(InsRangeTypes attribute) {
        return attribute == null ? null : attribute.getType();
    }

    @Override
    public InsRangeTypes convertToEntityAttribute(String dbData) {
        return InsRangeTypes.getType(dbData);
    }
}
