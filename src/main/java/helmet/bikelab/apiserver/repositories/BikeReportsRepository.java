package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeInfo;
import helmet.bikelab.apiserver.domain.bike.BikeReports;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeReportsRepository extends JpaRepository<BikeReports, Integer> {
    BikeReports findByBikeNo(Integer bikeNo);

}
