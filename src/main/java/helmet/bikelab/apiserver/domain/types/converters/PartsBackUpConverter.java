package helmet.bikelab.apiserver.domain.types.converters;

import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.types.ExpireTypes;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

public class PartsBackUpConverter extends Workspace implements AttributeConverter<List<Parts>, String> {

    @Override
    public String convertToDatabaseColumn(List<Parts> attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public List<Parts> convertToEntityAttribute(String dbData) {
        List<Parts> arrayList = getGsonInstance().fromJson(dbData, new ArrayList<Parts>().getClass());
        return arrayList;
    }

}
