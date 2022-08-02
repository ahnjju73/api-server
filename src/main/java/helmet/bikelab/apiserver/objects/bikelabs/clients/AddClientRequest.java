package helmet.bikelab.apiserver.objects.bikelabs.clients;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.ClientReceiptTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@Setter
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddClientRequest extends OriginObject {

    private String businessNo;
    private BusinessTypes businessType;
    private String email;
    private String groupId;
    private String direct;
    private String regNo;
    private ClientInfo clientInfo;
    private ModelAddress address;
    private String uuid;
    private String shopId;

    public void setAddress(Map address) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.address = objectMapper.convertValue(address, ModelAddress.class);
    }

    @JsonIgnore
    public void checkValidation(){
        if(!bePresent(clientInfo.getName())) withException("400-011");
        if(!bePresent(uuid)) withException("400-012");
        if(!bePresent(clientInfo.getPhone())) withException("400-013");
        if(!bePresent(email)) withException("400-014");
        if(BusinessTypes.CORPORATE.equals(this.businessType)){
            if(!bePresent(this.businessNo)) withException("400-010");
        }
        if(!bePresent(regNo)) withException("400-015");
        if(!bePresent(clientInfo.getRegDate())) withException("400-016");
        if(!bePresent(clientInfo.getRegBusinessType())) withException("400-018");
        if(!bePresent(clientInfo.getRegSectorType())) withException("400-017");
        if(!bePresent(address)) withException("400-019");
    }

}
