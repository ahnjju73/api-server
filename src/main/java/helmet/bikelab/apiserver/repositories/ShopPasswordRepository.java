package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.ShopInfo;
import helmet.bikelab.apiserver.domain.shops.ShopPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopPasswordRepository extends JpaRepository<ShopPassword, Integer> {
}
