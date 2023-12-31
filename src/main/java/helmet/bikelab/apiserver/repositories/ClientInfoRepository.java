package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientInfoRepository extends JpaRepository<ClientInfo, Integer> {
    ClientInfo findByClient(Clients clients);
    void deleteAllByClient_ClientId(String clientId);
    ClientInfo findByPhone(String phone);
    ClientInfo findByClientNo(Integer clientNo);
}
