package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUser;

import java.io.Serializable;

public class BikeUserSessionPK implements Serializable {

    private Integer bikeUserNo;

    private UserSessionTypes sessionTypes;

}
