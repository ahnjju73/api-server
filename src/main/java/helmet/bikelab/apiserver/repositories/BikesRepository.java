package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikesRepository extends JpaRepository<Bikes, Integer> {
    Bikes findByBikeId(String bikeId);
    Bikes findByCarNum(String bikeNum);
    Bikes findByVimNum(String vimNum);
}
