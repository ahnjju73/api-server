package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientAddresses;
import helmet.bikelab.apiserver.domain.client.ClientShop;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.ClientShopPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientShopRepository extends JpaRepository<ClientShop, ClientShopPK> {
    ClientShop findByClientNo(Integer clientNo);
}
