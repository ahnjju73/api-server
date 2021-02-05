package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ReadWriteTypes;

import javax.persistence.AttributeConverter;

public class ReadWriteTypesConverter implements AttributeConverter<ReadWriteTypes, String> {

    @Override
    public String convertToDatabaseColumn(ReadWriteTypes attribute) {
        return attribute.getAuth();
    }

    @Override
    public ReadWriteTypes convertToEntityAttribute(String dbData) {
        return ReadWriteTypes.getAuth(dbData);
    }

}
