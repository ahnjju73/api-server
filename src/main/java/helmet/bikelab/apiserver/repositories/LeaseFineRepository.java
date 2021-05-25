package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.lease.LeaseFine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaseFineRepository extends JpaRepository<LeaseFine, Integer> {
    LeaseFine findByFine_FineId(String fines);
    List<LeaseFine> findAllByLease_LeaseId(String leaseId);
}
