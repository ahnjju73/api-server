package helmet.bikelab.apiserver.domain.types.converters;

import com.google.common.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.types.ProvisionalPartsDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class PartsConverter extends Workspace implements AttributeConverter<List<ProvisionalPartsDto>, String> {
    @Override
    public String convertToDatabaseColumn(List<ProvisionalPartsDto> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<ProvisionalPartsDto> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<ProvisionalPartsDto>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return bePresent(o) ? (List<ProvisionalPartsDto>)o : null;
    }
}
