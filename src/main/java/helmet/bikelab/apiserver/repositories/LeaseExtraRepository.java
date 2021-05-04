package helmet.bikelab.apiserver.repositories;


import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseExtraRepository extends JpaRepository<LeaseExtras, Integer> {
    LeaseExtras findByLease_LeaseId(String leaseId);
}
