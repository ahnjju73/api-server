package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeRidersBak;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikeRiderBakRepository extends JpaRepository<BikeRidersBak, Integer> {

}
