package helmet.bikelab.apiserver.domain.types.converters;

import com.google.common.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.types.ProvisionalPartsDto;
import helmet.bikelab.apiserver.objects.ConsultingDescriptionDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class ConsultingDescriptionConverter extends Workspace implements AttributeConverter<List<ConsultingDescriptionDto>, String> {

    @Override
    public String convertToDatabaseColumn(List<ConsultingDescriptionDto> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<ConsultingDescriptionDto> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<ConsultingDescriptionDto>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return bePresent(o) ? (List<ConsultingDescriptionDto>)o : null;
    }
}
