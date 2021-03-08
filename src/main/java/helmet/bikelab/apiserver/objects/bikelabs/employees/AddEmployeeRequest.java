package helmet.bikelab.apiserver.objects.bikelabs.employees;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AddEmployeeRequest extends OriginObject {

    private String email;
//    private String password;
//    private String confirmPassword;
    private String phone;

    public void validation(){
        if(!bePresent(email)) withException("200-001");
//        String blankPassword = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
//        if(!bePresent(password) || !bePresent(confirmPassword)) withException("600-002");
//        if(blankPassword.equals(password)) withException("600-002");
//        if(!password.equals(confirmPassword)) withException("600-002");
    }

}

