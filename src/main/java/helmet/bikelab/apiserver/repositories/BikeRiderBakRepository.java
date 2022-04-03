package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeRidersBak;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeRiderBakRepository extends JpaRepository<BikeRidersBak, Integer> {
    void deleteAllByBikeNo(Integer bikeNo);
    List<BikeRidersBak> findAllByRider_RiderId(String riderId);
    BikeRidersBak findByRider_RiderIdAndBike_BikeId(String riderId, String bike_id);
}
