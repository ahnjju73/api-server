package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaseRepository extends JpaRepository<Leases, Integer> {
    Leases findByBikeNo(Integer bikeNo);
    Leases findByLeaseId(String leaseId);
    Boolean existsAllByInsuranceNoEquals(Integer insuranceNo);
    List<Leases> findAllByClients_ClientIdOrderByLeaseInfo_ContractDate(String clientId);
    List<Leases> findAllByBike_BikeId(String bikeId);
    List<Leases> findAllByClients_ClientIdAndStatusOrderByLeaseInfo_ContractDate(String clientId, LeaseStatusTypes leaseStatusTypes);
}
