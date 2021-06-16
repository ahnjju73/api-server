package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientPassword;
import helmet.bikelab.apiserver.domain.client.ClientSessions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSessionsRepository extends JpaRepository<ClientSessions, Integer> {
    void deleteAllByClient_ClientId(String clientId);
}
