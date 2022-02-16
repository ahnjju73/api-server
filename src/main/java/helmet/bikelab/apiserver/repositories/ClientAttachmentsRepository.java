package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientAttachments;
import helmet.bikelab.apiserver.domain.client.ClientManagers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAttachmentsRepository extends JpaRepository<ClientAttachments, Integer> {
    void deleteAllByClient_ClientId(String clientId);
}
