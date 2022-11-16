package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderInsurances;
import helmet.bikelab.apiserver.domain.types.RiderInsuranceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderInsuranceRepository extends JpaRepository<RiderInsurances, Integer> {
    Page<RiderInsurances> findAll(Pageable pageable);
    Page<RiderInsurances> findAllByRiderInsurancesDtl_RiderNameContaining(String riderName, Pageable pageable);
    Page<RiderInsurances> findAllByRiderInsurancesDtl_RiderInsuranceStatus(RiderInsuranceStatus riderInsuranceStatus, Pageable pageable);
    Page<RiderInsurances> findAllByRiderInsurancesDtl_RiderNameContainingAndRiderInsurancesDtl_RiderInsuranceStatus(String riderName, RiderInsuranceStatus riderInsuranceStatus, Pageable pageable);

    RiderInsurances findByRiderInsId(String riderInsId);
    void deleteByRiderInsId(String riderInsId);

}
