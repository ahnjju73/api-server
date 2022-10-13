package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.Chasoo;

import javax.persistence.AttributeConverter;

public class ChasooConveter implements AttributeConverter<Chasoo, String> {
    @Override
    public String convertToDatabaseColumn(Chasoo attribute) {
        return attribute != null ? attribute.getStatus() : null;
    }

    @Override
    public Chasoo convertToEntityAttribute(String dbData) {
        return Chasoo.getStatus(dbData);
    }
}
