package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.io.Serializable;

public class ClientShopPK implements Serializable {

    @Column(name = "client_no")
    private Integer clientNo;

    @Column(name = "shop_no")
    private Integer shopNo;

}
