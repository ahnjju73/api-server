package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderDemandLease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderDemandLeaseRepository extends JpaRepository<RiderDemandLease, Integer> {

    RiderDemandLease findByRiderNo(Integer riderNo);
    RiderDemandLease findByRider_RiderId(String riderId);
    Boolean existsByRiderNo(Integer riderNo);

}
