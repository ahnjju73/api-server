package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientInfoRepository extends JpaRepository<ClientInfo, Integer> {
    ClientInfo findByClient(Clients clients);
}
