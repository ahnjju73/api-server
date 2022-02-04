package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Settles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettleRepository extends JpaRepository<Settles, Long> {
    Settles findBySettleId(String settleId);
}
