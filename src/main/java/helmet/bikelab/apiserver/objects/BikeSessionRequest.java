package helmet.bikelab.apiserver.objects;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.ProgramUser;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BikeSessionRequest extends SessionRequest {

    private BikeUser sessionUser;

    private ProgramUser programUser;

}
