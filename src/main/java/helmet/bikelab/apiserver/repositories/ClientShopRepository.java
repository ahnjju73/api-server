package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientAddresses;
import helmet.bikelab.apiserver.domain.client.ClientShop;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.ClientShopPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientShopRepository extends JpaRepository<ClientShop, ClientShopPK> {
    ClientShop findByClientNo(Integer clientNo);
    List<ClientShop> findAllByClientNo(Integer clientNo);
}
