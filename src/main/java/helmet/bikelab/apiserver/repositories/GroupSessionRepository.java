package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.GroupSessions;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupSessionRepository extends JpaRepository<GroupSessions, Integer> {
    void deleteByGroup_GroupId(String groupId);
}
