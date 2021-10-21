package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.Activities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivitiesRepository extends JpaRepository<Activities, Long> {

}
