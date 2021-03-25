package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Fines;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinesRepository extends JpaRepository<Fines, Integer> {
    Fines findByFineNum(String fineNum);
}
