package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderDemandLeaseSpecialTerms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface RiderDemandLeaseTermsRepository extends JpaRepository<RiderDemandLeaseSpecialTerms, Integer> {

    void deleteAllByRiderNo(Integer riderNo);
}
