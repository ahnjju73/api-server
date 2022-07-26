package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Notifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
    @Query(nativeQuery = true, value = "select n.* from notifications n join notification_targets nt on nt.notification_no = n.notification_no where nt.notification_type in ?1 group by n.notification_no")
    Page<Notifications> getNotificationsByType(List<String> notificationType, Pageable page);
    Page<Notifications> findAll(Pageable pageable);
    void deleteByNotificationNo(Integer notificationNo);

}
