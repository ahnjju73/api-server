package helmet.bikelab.apiserver.repositories;


import helmet.bikelab.apiserver.domain.client.GroupAddresses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientGroupAddressRepository extends JpaRepository<GroupAddresses, Integer> {
    void deleteByGroup_GroupId(String groupId);
}
