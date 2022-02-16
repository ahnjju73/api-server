package helmet.bikelab.apiserver.domain.types;


import helmet.bikelab.apiserver.domain.types.converters.RiderAddressTypeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Convert;
import java.io.Serializable;

@Getter
@Setter
public class RiderAddressPK implements Serializable {
    @Column(name = "rider_no")
    private Integer riderNo;

    @Column(name = "address_type", columnDefinition = "ENUM")
    @Convert(converter = RiderAddressTypeConverter.class)
    private RiderAddressTypes riderAddressType;
}
