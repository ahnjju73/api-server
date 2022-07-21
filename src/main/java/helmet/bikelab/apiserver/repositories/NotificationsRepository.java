package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {


}
