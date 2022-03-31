package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.embeds.ModelInsuranceImage;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class ModelInsuranceImageConverter extends Workspace implements AttributeConverter<List<ModelInsuranceImage>, String> {
    @Override
    public String convertToDatabaseColumn(List<ModelInsuranceImage> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<ModelInsuranceImage> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<ModelInsuranceImage>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<ModelInsuranceImage>)o;
    }
}
