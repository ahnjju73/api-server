package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeaseInsurances;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseInsurancesRepository extends JpaRepository<LeaseInsurances, Integer> {
    LeaseInsurances findByLease_LeaseId(String leaseId);
    void deleteAllByLease_LeaseId(String leaseId);
}
