package helmet.bikelab.apiserver.objects.bikelabs.clients.group;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddGroupRequest extends OriginObject {
    private String groupName;
    private String ceoName;
    private String ceoEmail;
    private String ceoPhone;
    private String regNo;
    private String email;
    private ModelAddress address;

    public void checkValidation(){
        if(!bePresent(this.groupName)) withException("300-003");
        if(!bePresent(this.regNo)) withException("300-008");
        if(!bePresent(this.ceoName)) withException("300-009");
        if(!bePresent(this.email)) withException("300-010");
        if(!bePresent(this.ceoPhone)) withException("300-011");
        if(!bePresent(this.address)) withException("300-012");
    }
}
