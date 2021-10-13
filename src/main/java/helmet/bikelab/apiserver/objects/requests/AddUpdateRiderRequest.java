package helmet.bikelab.apiserver.objects.requests;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddUpdateRiderRequest extends OriginObject {
    private String riderId;
    private String email;
    private String name;
    private String phone;

    public void checkValidation(){
        if(!bePresent(email))
            withException("950-001");
        if(!bePresent(name))
            withException("950-002");
        if(!bePresent(phone))
            withException("950-003");
    }
}
