package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientGroups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientGroupRepository extends JpaRepository<ClientGroups, Integer> {
    ClientGroups findByGroupId(String groupId);
    void deleteByGroupId(String groupId);
}