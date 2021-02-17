package helmet.bikelab.apiserver.objects.bikelabs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewBikeUserDto {
    String name;
    String email;
    String phone;
    String thumbnail;
    String intro;
    String namingTest;
}
