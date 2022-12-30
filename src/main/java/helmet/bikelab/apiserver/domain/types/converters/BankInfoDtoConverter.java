package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.objects.BankInfoDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;

public class BankInfoDtoConverter extends Workspace implements AttributeConverter<BankInfoDto, String> {
    @Override
    public String convertToDatabaseColumn(BankInfoDto attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public BankInfoDto convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<BankInfoDto>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (BankInfoDto)o;
    }
}
