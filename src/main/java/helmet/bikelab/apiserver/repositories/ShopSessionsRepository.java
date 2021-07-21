package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.ShopInfo;
import helmet.bikelab.apiserver.domain.shops.ShopSessions;
import helmet.bikelab.apiserver.domain.types.ShopSessionPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopSessionsRepository extends JpaRepository<ShopSessions, ShopSessionPK> {
}
