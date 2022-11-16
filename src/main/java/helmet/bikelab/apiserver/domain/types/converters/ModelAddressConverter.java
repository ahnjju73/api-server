package helmet.bikelab.apiserver.domain.types.converters;

import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.objects.AddressDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class ModelAddressConverter extends Workspace implements AttributeConverter<AddressDto, String> {
    @Override
    public String convertToDatabaseColumn(AddressDto attribute) {
        String toJson = getJson(attribute);
        return toJson;
    }

    @Override
    public AddressDto convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<AddressDto>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (AddressDto)o;
    }
}
