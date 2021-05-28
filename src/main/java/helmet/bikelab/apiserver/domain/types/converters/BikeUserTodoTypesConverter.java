package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.BikeUserTodoTypes;

import javax.persistence.AttributeConverter;

public class BikeUserTodoTypesConverter implements AttributeConverter<BikeUserTodoTypes, String> {

    @Override
    public String convertToDatabaseColumn(BikeUserTodoTypes bikeUserTodoTypes) {
        return bikeUserTodoTypes.getStatus();
    }

    @Override
    public BikeUserTodoTypes convertToEntityAttribute(String bikeUserTodoTypes) {
        return BikeUserTodoTypes.getBikeUserTodoTypes(bikeUserTodoTypes);
    }

}
