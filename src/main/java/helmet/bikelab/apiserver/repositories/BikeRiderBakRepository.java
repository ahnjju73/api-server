package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeRidersBak;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeRiderBakRepository extends JpaRepository<BikeRidersBak, Integer> {
    List<BikeRidersBak> findAllByRider_RiderId(String riderId);
}
