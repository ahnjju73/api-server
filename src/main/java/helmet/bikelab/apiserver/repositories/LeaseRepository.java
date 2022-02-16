package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStopStatusTypes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaseRepository extends JpaRepository<Leases, Integer> {
    Leases findByBike_BikeId(String bikeId);
    Leases findByLeaseNo(Integer leaseNo);
    Leases findByBikeNo(Integer bikeNo);
    Leases findByLeaseId(String leaseId);
    Boolean existsAllByInsuranceNoEquals(Integer insuranceNo);
    List<Leases> findAllByClients_ClientIdOrderByLeaseInfo_ContractDate(String clientId);
    List<Leases> findAllByClients_ClientId(String clientId);
    List<Leases> findAllByBike_BikeId(String bikeId);
    List<Leases> findAllByClients_ClientIdAndStatusOrderByLeaseInfo_ContractDate(String clientId, LeaseStatusTypes leaseStatusTypes);
    List<Leases> findAllByStatusAndLeaseStopStatus(LeaseStatusTypes lst, LeaseStopStatusTypes lsst);
    List<Leases> findAllByDemandLeases_DemandLeaseId(String demandLeaseId, Pageable pageable);
    Integer countAllByInsuranceNo(Integer insuranceNo);
}
