package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderDemandLeaseAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderDemandLeaseAttachmentsRepository extends JpaRepository<RiderDemandLeaseAttachments, Integer> {
    void deleteByDemandLeaseFileNoAndRiderNo(Long fileNo, Integer riderNo);
}
