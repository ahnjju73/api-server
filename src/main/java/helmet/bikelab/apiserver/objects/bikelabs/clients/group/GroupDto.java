package helmet.bikelab.apiserver.objects.bikelabs.clients.group;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.ClientGroups;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GroupDto {

    private String groupId;
    private String groupName;

    public GroupDto(ClientGroups clientGroups){
        this.groupId = clientGroups.getGroupId();
        this.groupName = clientGroups.getGroupName();
    }
}
