package helmet.bikelab.apiserver.objects.requests.shops;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DeleteShopAttachmentRequest {

    private String shopId;
    private String uuid;

}
