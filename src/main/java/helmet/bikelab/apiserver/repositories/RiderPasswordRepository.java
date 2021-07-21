package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderPasswordRepository extends JpaRepository<RiderPassword, Integer> {

    RiderPassword findByRider_Email(String email);

}
