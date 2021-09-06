package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.types.ExpireTypes;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PartsBackUpConverter extends Workspace implements AttributeConverter<List<PartsBackUpDto>, String> {

    @Override
    public String convertToDatabaseColumn(List<PartsBackUpDto> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<PartsBackUpDto> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<PartsBackUpDto>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<PartsBackUpDto>)o;
    }

}
