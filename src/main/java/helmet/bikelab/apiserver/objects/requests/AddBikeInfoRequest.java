package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddBikeInfoRequest extends BikeInfoDto {

    public void checkValidation(){
        if(!bePresent(this.getInfoType())) writeMessage("분류를 선택해주세요.");
        if(!bePresent(this.getPrice())) writeMessage("금액을 입력해주세요.");
        if(!bePresent(this.getPaidAt())) writeMessage("지출일자를 입력해주세요.");
    }

}
