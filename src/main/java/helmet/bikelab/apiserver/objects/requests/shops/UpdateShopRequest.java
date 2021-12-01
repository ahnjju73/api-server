package helmet.bikelab.apiserver.objects.requests.shops;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UpdateShopRequest extends AddShopRequest {

    private String shopId;

}
