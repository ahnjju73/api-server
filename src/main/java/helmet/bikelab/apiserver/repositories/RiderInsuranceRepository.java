package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderInsurances;
import helmet.bikelab.apiserver.domain.types.RiderInsuranceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiderInsuranceRepository extends JpaRepository<RiderInsurances, Integer> {
    Page<RiderInsurances> findAll(Pageable pageable);

    RiderInsurances findByRiderInsId(String riderInsId);
    Page<RiderInsurances> findByRiderNameContaining(String riderName, Pageable pageable);
    void deleteByRiderInsId(String riderInsId);

}
