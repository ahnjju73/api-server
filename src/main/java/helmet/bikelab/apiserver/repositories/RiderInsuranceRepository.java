package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderInsurances;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderInsuranceRepository extends JpaRepository<RiderInsurances, Integer> {
}
