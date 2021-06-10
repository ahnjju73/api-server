package helmet.bikelab.apiserver.objects.bikelabs;

import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.EmailCheck;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewBikeUserDto extends OriginObject {
    private String name;
    private String email;
    private String phone;
    private String thumbnail;
    private String intro;
    private String namingTest;

    public void checkValidation(){
        if(!bePresent(this.name)) withException("101-002");
        if(!bePresent(this.email)) withException("101-003");
        if(!bePresent(this.phone)) withException("101-004");
        if(!EmailCheck.isValidEmailAddress(this.email)) withException("101-005");
    }

}
