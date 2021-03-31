package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientPassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientPasswordRepository extends JpaRepository<ClientPassword, Integer> {
    ClientPassword findByClientNo(Integer clientNo);
}
