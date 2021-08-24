package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.Parts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartsRepository extends JpaRepository<Parts, Long> {
}
