package helmet.bikelab.apiserver.domain.types;



import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.io.Serializable;

public class GroupSessionPK implements Serializable {

    @Column(name = "group_no")
    private Integer groupNo;

    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

}