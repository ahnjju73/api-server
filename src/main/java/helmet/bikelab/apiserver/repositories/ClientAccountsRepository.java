package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAccountsRepository extends JpaRepository<ClientAccounts, Integer> {
    void deleteAllByClient_ClientId(String clientId);
}
