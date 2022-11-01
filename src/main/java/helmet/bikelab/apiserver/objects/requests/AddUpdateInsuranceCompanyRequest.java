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

    private String id;
    private String chasoo;
    private String role;
    private String companyName;
    private String deptName;
    private String deptCenter;
    private String position;
    private String positionRole;
    private String name;
    private String email;
    private String phone;
    private List<PresignedURLVo> images;

    public void validationCheck(){

        if(images.size() != 0)
            for (PresignedURLVo pvo: images) {
                pvo.checkValidation();
            }
    }
}
