package helmet.bikelab.apiserver.domain.types;

import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;

import javax.persistence.*;
import java.io.Serializable;

public class PartsTypeDiscountClientPK implements Serializable {
    @Column(name = "parts_type_no", nullable = false)
    private Integer partsTypeNo;
    @Column(name = "client_no")
    private Integer clientNo;
}
