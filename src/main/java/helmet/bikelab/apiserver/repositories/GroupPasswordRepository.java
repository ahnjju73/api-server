package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.client.GroupPasswords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPasswordRepository extends JpaRepository<GroupPasswords, Integer> {
    GroupPasswords findByGroup_CeoEmail(String email);
}
