package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.Riders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Riders, Integer> {
    Riders findByPhone(String phone);
}
