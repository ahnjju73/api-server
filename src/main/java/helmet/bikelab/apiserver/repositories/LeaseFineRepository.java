package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.lease.LeaseFine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseFineRepository extends JpaRepository<LeaseFine, Integer> {
    LeaseFine findByFine(Fines fines);
}
