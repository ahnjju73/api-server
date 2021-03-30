package helmet.bikelab.apiserver.objects.bikelabs.clients;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddClientRequest extends OriginObject {
    private String email;
    private String groupId;
    private String direct;
    private String regNo;
    private String clientDescription;
    private ClientInfo clientInfo;
    private ModelAddress address;

    public void setAddress(Map address) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.address = objectMapper.convertValue(address, ModelAddress.class);
    }

}
