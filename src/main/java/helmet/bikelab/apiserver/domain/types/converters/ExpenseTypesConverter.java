package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ExpenseTypes;

import javax.persistence.AttributeConverter;

public class ExpenseTypesConverter implements AttributeConverter<ExpenseTypes, String> {
    @Override
    public String convertToDatabaseColumn(ExpenseTypes attribute) {
        return attribute.getType();
    }

    @Override
    public ExpenseTypes convertToEntityAttribute(String dbData) {
        return ExpenseTypes.getType(dbData);
    }
}
