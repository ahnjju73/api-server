package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderInfoRepository extends JpaRepository<RiderInfo, Integer> {
}
