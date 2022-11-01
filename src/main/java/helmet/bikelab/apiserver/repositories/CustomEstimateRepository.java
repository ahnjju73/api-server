package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CustomEstimates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomEstimateRepository extends JpaRepository<CustomEstimates, Long> {

    CustomEstimates findByCustomEstimateId(String estimateId);
}
