package helmet.bikelab.apiserver.objects.bikelabs.users;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserProfileRequest extends UserProfileResponse{
    private String password;
    private String confirmPassword;

    public void validate(){
        String blankPassword = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        if(!bePresent(password) || !bePresent(confirmPassword) || blankPassword.equals(password) || blankPassword.equals(confirmPassword))
            notSavePassword();
        else {
            if(!password.equals(confirmPassword)) withException("100-001");
        }

    }

    private void notSavePassword(){
        this.password = null;
        this.confirmPassword = null;
    }
}
