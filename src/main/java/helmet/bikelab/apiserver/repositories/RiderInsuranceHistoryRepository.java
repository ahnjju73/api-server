package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderInsuranceHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderInsuranceHistoryRepository extends JpaRepository<RiderInsuranceHistories, Integer> {
    RiderInsuranceHistories findByRiderInsurance_RiderInsId(String riderInsId);
    void deleteAllByRiderInsurance_RiderInsId(String riderInsId);
}
