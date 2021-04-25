package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Leases;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseRepository extends JpaRepository<Leases, Integer> {
    Leases findByBikeNo(Integer bikeNo);
    Leases findByLeaseId(String leaseId);
}
