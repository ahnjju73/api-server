package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderInsurancesDtl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiderInsuranceDtlRepository extends JpaRepository<RiderInsurancesDtl, Integer> {
    RiderInsurancesDtl findTopByRiderInsurances_RiderInsIdOrderByDtlNoDesc(String riderInsId);
    List<RiderInsurancesDtl> findAllByRiderInsurances_RiderInsIdOrderByCreatedAtDesc(String riderInsId);
    void deleteAllByRiderInsurances_RiderInsId(String riderInsId);
    RiderInsurancesDtl findByDtlNo(Integer dtlNo);
    Integer countAllByRiderInsurances_RiderInsId(String riderInsId);
}
