package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.io.Serializable;

public class BikeUserSessionPK implements Serializable {

    @Column(name = "user_no")
    private Integer bikeUserNo;

    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

}
