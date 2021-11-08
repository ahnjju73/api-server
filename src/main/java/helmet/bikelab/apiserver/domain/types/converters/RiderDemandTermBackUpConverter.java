package helmet.bikelab.apiserver.domain.types.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import helmet.bikelab.apiserver.domain.riders.RiderDemandLeaseAttachments;
import helmet.bikelab.apiserver.domain.riders.RiderDemandLeaseSpecialTerms;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.PartsBackUpDto;
import helmet.bikelab.apiserver.services.internal.Workspace;

import javax.persistence.AttributeConverter;
import java.lang.reflect.Type;
import java.util.List;

public class RiderDemandTermBackUpConverter extends Workspace implements AttributeConverter<List<RiderDemandLeaseSpecialTerms>, String> {
    @Override
    public String convertToDatabaseColumn(List<RiderDemandLeaseSpecialTerms> attribute) {
        ObjectMapper mapper = new ObjectMapper();
        String toJson = "";
        try {
            toJson = mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if(toJson.equals(""))
            toJson = null;
        return toJson;
    }

    @Override
    public List<RiderDemandLeaseSpecialTerms> convertToEntityAttribute(String dbData) {
        Type type = new TypeToken<List<PartsBackUpDto>>() {}.getType();
        Object o = getGsonInstance().fromJson(dbData, type);
        return !bePresent(o) ? null : (List<RiderDemandLeaseSpecialTerms>)o;
    }
}
