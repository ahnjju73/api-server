package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.domain.types.BikeInfoTypes;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BikeInfoDto extends OriginObject {

    private BikeInfoTypes infoType;
    private Integer price;
    private LocalDate paidAt;

    public void setPaidAt(String paidAt) {
        this.paidAt = LocalDate.parse(paidAt);
    }

    public void setInfoType(String infoType) {
        this.infoType = BikeInfoTypes.getBikeInfoTypes(infoType);
    }
}
