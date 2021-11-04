package helmet.bikelab.apiserver.domain.types.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.riders.RiderDemandLeaseAttachments;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

public class RiderDemandAttachmentBackUpConverter extends Workspace implements AttributeConverter<List<RiderDemandLeaseAttachments>, String> {
    @Override
    public String convertToDatabaseColumn(List<RiderDemandLeaseAttachments> attribute) {
        ObjectMapper mapper = new ObjectMapper();
        String toJson = "";
        try {
            toJson = mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return toJson;
    }

    @Override
    public List<RiderDemandLeaseAttachments> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<PartsBackUpDto>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<RiderDemandLeaseAttachments>)o;
    }
}
