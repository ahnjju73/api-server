package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.ShopInfo;
import helmet.bikelab.apiserver.domain.shops.Shops;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopInfoRepository extends JpaRepository<ShopInfo, Integer> {
}
