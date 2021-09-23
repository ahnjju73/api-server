package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.bike.PartsImages;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class PartsImagesConverter extends Workspace implements AttributeConverter<List<PartsImages>, String> {

    @Override
    public String convertToDatabaseColumn(List<PartsImages> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<PartsImages> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<PartsImages>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<PartsImages>)o;
    }

}
