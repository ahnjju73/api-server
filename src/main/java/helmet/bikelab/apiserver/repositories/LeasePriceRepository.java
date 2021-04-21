package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeasePrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeasePriceRepository extends JpaRepository<LeasePrice, Integer> {
    LeasePrice findByLease_LeaseId(String leaseId);
}
