package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientAddresses;
import helmet.bikelab.apiserver.domain.client.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAddressesRepository extends JpaRepository<ClientAddresses, Integer> {
    ClientAddresses findByClient(Clients clients);
    void deleteAllByClient_ClientId(String clientId);
}
