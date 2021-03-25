package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikesRepository extends JpaRepository<Bikes, Integer> {
    Bikes findByBikeId(String bikeId);
}
