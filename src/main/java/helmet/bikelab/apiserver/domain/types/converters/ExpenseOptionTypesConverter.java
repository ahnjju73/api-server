package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.types.ExpenseOptionTypes;

import javax.persistence.AttributeConverter;

public class ExpenseOptionTypesConverter implements AttributeConverter<ExpenseOptionTypes, String> {
    @Override
    public String convertToDatabaseColumn(ExpenseOptionTypes attribute) {
        return attribute == null ? null : attribute.getType();
    }

    @Override
    public ExpenseOptionTypes convertToEntityAttribute(String dbData) {
        return ExpenseOptionTypes.getType(dbData);
    }
}
