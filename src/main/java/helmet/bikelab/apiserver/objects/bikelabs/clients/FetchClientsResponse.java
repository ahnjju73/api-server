package helmet.bikelab.apiserver.objects.bikelabs.clients;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchClientsResponse extends OriginObject {
    private String clientId;
    private String clientName;
    private String clientPhone;
    private String managerName;
    private String managerPhone;
    private String managerEmail;
}
