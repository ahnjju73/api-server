package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartsTypesRepository extends JpaRepository<PartsTypes, Integer> {
}
