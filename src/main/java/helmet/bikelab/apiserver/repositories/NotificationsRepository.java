package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Notifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
    @Query(nativeQuery = true, value = "select * from notifications n inner join notification_targets nt on nt.notification_no = n.notification_no where nt.notification_type = ?1")
    Page<Notifications> getNotificationsByType(String notificationType, Pageable page);
    Page<Notifications> findAll(Pageable pageable);
    void deleteByNotificationNo(Integer notificationNo);

}
