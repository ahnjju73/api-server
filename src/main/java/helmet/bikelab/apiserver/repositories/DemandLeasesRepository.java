package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandLeasesRepository extends JpaRepository<DemandLeases, String> {

    List<DemandLeases> findAllByClient_ClientId(String clientId);
    DemandLeases findByDemandLeaseId(String demandLeaseId);
    DemandLeases findByDemandLeaseIdAndClientNo(String demandLeaseId, Integer clientNo);
    void deleteByDemandLeaseNo(Long demandLeaseNo);
    DemandLeases findByDemandLeaseNo(Long demandLeaseNo);
    List<DemandLeases> findAllByCarModelCode(String carModelCode);

}
