package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import java.io.Serializable;

public class ShopSessionPK implements Serializable {

    @Id
    @Column(name = "shop_no")
    private Integer shopNo;

    @Id
    @Column(name = "session_type", columnDefinition = "ENUM")
    @Convert(converter = UserSessionTypeConverter.class)
    private UserSessionTypes sessionTypes;

}
