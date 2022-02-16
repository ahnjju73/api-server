package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderAddresses;
import helmet.bikelab.apiserver.domain.types.RiderAddressPK;
import helmet.bikelab.apiserver.domain.types.RiderAddressTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderAddressRepository extends JpaRepository<RiderAddresses, RiderAddressPK> {
    RiderAddresses findByRider_RiderIdAndRiderAddressType(String riderId, RiderAddressTypes type);
    RiderAddresses findByRiderNoAndRiderAddressType(Integer riderNo, RiderAddressTypes type);

}
