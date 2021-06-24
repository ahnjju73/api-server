package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientsRepository extends JpaRepository<Clients, Integer> {
    Clients findByClientId(String clientId);
    List<Clients> findByClientGroup_GroupId(String groupId);
    Integer countAllByClientGroup_GroupId(String groupId);
    Clients findByEmail(String email);
    void deleteByClientId(String clientId);
    Clients findByRegNum(String regNum);
    Clients findByClientInfo_Name(String clientName);
}
