package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderSessions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderSessionsRepository extends JpaRepository<RiderSessions, Integer> {
}
