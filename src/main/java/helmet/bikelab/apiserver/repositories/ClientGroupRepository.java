package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.ClientGroups;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientGroupRepository extends JpaRepository<ClientGroups, Integer> {
    ClientGroups findByGroupId(String groupId);
}
