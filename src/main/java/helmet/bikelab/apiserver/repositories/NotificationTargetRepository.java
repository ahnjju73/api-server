package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.NotificationTargets;
import helmet.bikelab.apiserver.domain.types.NotificationTargetPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationTargetRepository extends JpaRepository<NotificationTargets, NotificationTargetPK> {
    List<NotificationTargets> findAllByNotificationNo(Integer notificationNo);
}
