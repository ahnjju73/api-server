package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateInsuranceCompanyRequest extends OriginObject {
    private String name;
    private String email;
    private String phone;
    private List<PresignedURLVo> images;

    public void validationCheck(){
        if(!bePresent(name))
            withException("");
        if(!bePresent(email))
            withException("");
        if(!bePresent(phone))
            withException("");
        if(images.size() != 0)
            for (PresignedURLVo pvo: images) {
                pvo.checkValidation();
            }
    }
}
