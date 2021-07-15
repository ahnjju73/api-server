package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientSessions;
import helmet.bikelab.apiserver.domain.types.ClientSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientSessionsRepository extends JpaRepository<ClientSessions, ClientSessionPK> {
    void deleteAllByClient_ClientId(String clientId);
    void deleteAllByClientNoAndSessionTypes(Integer clientNo, UserSessionTypes userSessionTypes);
}
