package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.objects.RiderInsHistoriesDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class HistoryConverter extends Workspace implements AttributeConverter<List<RiderInsHistoriesDto>, String> {
    @Override
    public String convertToDatabaseColumn(List<RiderInsHistoriesDto> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<RiderInsHistoriesDto> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<RiderInsHistoriesDto>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<RiderInsHistoriesDto>)o;    }
}
