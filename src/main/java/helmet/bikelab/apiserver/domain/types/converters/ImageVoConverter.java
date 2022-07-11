package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class ImageVoConverter extends Workspace implements AttributeConverter<List<ImageVo>, String> {

    @Override
    public String convertToDatabaseColumn(List<ImageVo> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<ImageVo> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<ImageVo>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<ImageVo>)o;
    }

}
