package helmet.bikelab.apiserver.services.internal;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.objects.SessionRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BikeSessionService extends SessionRequest {

    BikeUser sessionUser;


}
