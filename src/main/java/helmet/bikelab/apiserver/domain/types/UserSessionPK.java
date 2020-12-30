package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUser;

import java.io.Serializable;

public class UserSessionPK implements Serializable {

    private BikeLabUser user;

    private UserSessionTypes sessionTypes;

}
