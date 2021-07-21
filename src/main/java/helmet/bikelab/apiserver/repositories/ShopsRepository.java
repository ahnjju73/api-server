package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.shops.Shops;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopsRepository extends JpaRepository<Shops, Integer> {
}
