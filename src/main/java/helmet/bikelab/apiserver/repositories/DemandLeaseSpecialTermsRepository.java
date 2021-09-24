package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.demands.DemandLeaseSpecialTerms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandLeaseSpecialTermsRepository extends JpaRepository<DemandLeaseSpecialTerms, String> {
    void deleteAllByDemandLeaseNo(Long demandLeaseNo);
    List<DemandLeaseSpecialTerms> findAllByDemandLeaseNo(Long demandLeaseNo);
}
