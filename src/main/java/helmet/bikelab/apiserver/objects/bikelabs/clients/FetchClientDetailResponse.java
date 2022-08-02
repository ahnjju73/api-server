package helmet.bikelab.apiserver.objects.bikelabs.clients;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FetchClientDetailResponse extends OriginObject {
    private String clientId;
    private String groupName;
    private String groupId;
    private String direct;
    private String email;
    private String regNo;
    private ClientInfo clientInfo;
    private ModelAddress address;
    private String uuid;
    private Double discountRate;
    private String shopId;
    private String shopName;

    private String businessNo;
    private BusinessTypes businessType;
    private String businessTypeCode;
}
