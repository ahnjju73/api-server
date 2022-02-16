package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientOverpay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientOverpayRepository extends JpaRepository<ClientOverpay, Long> {
    List<ClientOverpay> findAllByClientNo(Integer clientNo);
    void deleteAllByClient_ClientId(String clientId);
}
