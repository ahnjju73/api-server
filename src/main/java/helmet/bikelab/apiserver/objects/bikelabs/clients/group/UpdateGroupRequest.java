package helmet.bikelab.apiserver.objects.bikelabs.clients.group;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateGroupRequest extends OriginObject {
    private String groupId;
    private String groupName;
    private String ceoName;
    private String ceoEmail;
    private String ceoPhone;
    private String businessNum;

    public void checkValidation(){
        if(!bePresent(groupName)) withException("300-003");
        if(!bePresent(groupId)) withException("300-004");

    }
}
