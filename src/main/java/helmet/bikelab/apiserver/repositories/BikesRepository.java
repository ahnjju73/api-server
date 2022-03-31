package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikesRepository extends JpaRepository<Bikes, Integer> {
    Bikes findByBikeNo(Integer bikeNo);
    Bikes findByBikeId(String bikeId);
    Bikes findByCarNum(String bikeNum);
    Bikes findByVimNum(String vimNum);
    Integer countAllByCarNum(String carNum);
    Integer countAllByVimNum(String vimNum);
    Bikes findByBikeIdAndRiders_RiderId(String bikeId, String riderId);
    List<Bikes> findAllByRiderNo(Integer riderNo);
    List<Bikes> findByRiderNoIsNotNull(Pageable pageable);
    Integer countAllByRiderNoIsNotNull();
}
