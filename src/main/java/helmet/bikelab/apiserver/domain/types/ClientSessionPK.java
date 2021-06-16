package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import java.io.Serializable;

public class ClientSessionPK implements Serializable {

    @Id
    @Column(name = "client_no")
    private Integer clientNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

}
