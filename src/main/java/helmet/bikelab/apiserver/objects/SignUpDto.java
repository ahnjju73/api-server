package helmet.bikelab.apiserver.objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDto {
    String name;
    String email;
    String phone;
    String thumbnail;
    String intro;
}
