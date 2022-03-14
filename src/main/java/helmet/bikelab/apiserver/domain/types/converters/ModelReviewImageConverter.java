package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.embeds.ModelReviewImage;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class ModelReviewImageConverter extends Workspace implements AttributeConverter<List<ModelReviewImage>, String> {

    @Override
    public String convertToDatabaseColumn(List<ModelReviewImage> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<ModelReviewImage> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<ModelReviewImage>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<ModelReviewImage>)o;
    }

}
