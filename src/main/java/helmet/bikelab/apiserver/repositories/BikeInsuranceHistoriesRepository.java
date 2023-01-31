package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeInsuranceHistories;
import helmet.bikelab.apiserver.domain.bike.BikeInsurances;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeInsuranceHistoriesRepository extends JpaRepository<BikeInsuranceHistories, Long> {
    List<BikeInsuranceHistories> findAllByInsuranceNoOrderByHistoryNoDesc(Integer insuranceNo);

}
