package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientManagers;
import helmet.bikelab.apiserver.domain.client.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientManagersRepository extends JpaRepository<ClientManagers, Long> {
    void deleteAllByClient_ClientId(String clientId);
}
