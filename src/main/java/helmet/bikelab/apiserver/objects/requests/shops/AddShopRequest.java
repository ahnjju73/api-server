package helmet.bikelab.apiserver.objects.requests.shops;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddShopRequest extends OriginObject {

    private String name;
    private String managerName;
    private String businessType;
    private String regNum;
    private String phone;
    private String email;
    private String startTime;
    private String endTime;
    private ModelAddress address;
    private Double longitude;
    private Double latitude;
    private String bankCd;
    private String account;
    private String depositor;

    public void setAddress(Map address) {
        ObjectMapper objectMapper = new ObjectMapper();
        this.address = objectMapper.convertValue(address, ModelAddress.class);
    }

    public void checkValidation(){
        if(!bePresent(name)) withException("401-001");
        if(!bePresent(managerName)) withException("401-002");
        if(!bePresent(businessType)) withException("401-012");
        if(!bePresent(regNum)) withException("401-003");
        if(!bePresent(email)) withException("401-007");
        if(!bePresent(phone)) withException("401-004");
        if(!bePresent(startTime)) withException("401-005");
        if(!bePresent(endTime)) withException("401-006");
    }

}
