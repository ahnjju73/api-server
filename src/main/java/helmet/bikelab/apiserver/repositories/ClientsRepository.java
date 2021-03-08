package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientsRepository extends JpaRepository<Clients, Integer> {

    Clients findByClientNo(Integer clientNo);

//    Optional <Clients> findByEmail(String email);
//
//    Optional<Clients> findByEmailAndUserNoNot(String email, Integer userNo);
//
//    Clients findByUserNoAndUserStatusTypes(Integer userNo, Clie bikeUserStatusTypes);
}
