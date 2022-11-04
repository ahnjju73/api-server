package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderInsurancesDtl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderInsuranceDtlRepository extends JpaRepository<RiderInsurancesDtl, Integer> {

}
