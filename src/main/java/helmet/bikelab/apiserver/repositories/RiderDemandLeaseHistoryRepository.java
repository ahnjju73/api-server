package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderDemandLeaseHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderDemandLeaseHistoryRepository extends JpaRepository<RiderDemandLeaseHistories, Integer> {
    RiderDemandLeaseHistories findByLease_LeaseId(String leaseId);
}
