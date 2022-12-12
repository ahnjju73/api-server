package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.embeds.ModelReviewImage;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ModelAttachmentConverter extends Workspace implements AttributeConverter<List<ModelAttachment>, String> {
    @Override
    public String convertToDatabaseColumn(List<ModelAttachment> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<ModelAttachment> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<ModelAttachment>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? new ArrayList<>() : (List<ModelAttachment>)o;
    }
}
