package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;

public class RiderInfoDtoConverter extends Workspace implements AttributeConverter<RiderInfoDto, String> {
    @Override
    public String convertToDatabaseColumn(RiderInfoDto attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public RiderInfoDto convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<RiderInfoDto>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (RiderInfoDto)o;
    }
}
