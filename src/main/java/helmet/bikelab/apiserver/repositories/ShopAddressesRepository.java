package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.ShopAddresses;
import helmet.bikelab.apiserver.domain.shops.Shops;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopAddressesRepository extends JpaRepository<ShopAddresses, Integer> {
}
