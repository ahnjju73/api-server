package helmet.bikelab.apiserver.domain.riders;


import com.fasterxml.jackson.annotation.JsonIgnore;
import helmet.bikelab.apiserver.domain.embeds.ModelAddress;
import helmet.bikelab.apiserver.domain.types.RiderAddressPK;
import helmet.bikelab.apiserver.domain.types.RiderAddressTypes;
import helmet.bikelab.apiserver.domain.types.converters.RiderAddressTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "rider_addresses", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@IdClass(RiderAddressPK.class)
@NoArgsConstructor
public class RiderAddresses {

    @Id
    @Column(name = "rider_no", nullable = false)
    private Integer riderNo;

    @Id
    @Column(name = "address_type", columnDefinition = "ENUM")
    @Convert(converter = RiderAddressTypeConverter.class)
    private RiderAddressTypes riderAddressTypes;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "rider_no", insertable = false, updatable = false)
    private Riders rider;

    @Embedded
    private ModelAddress modelAddress = new ModelAddress();
}
