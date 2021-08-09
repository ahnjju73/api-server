package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.demands.DemandLeaseAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DemandLeaseAttachmentsRepository extends JpaRepository<DemandLeaseAttachments, Long> {
    List<DemandLeaseAttachments> findAllByDemandLeases_DemandLeaseId(String demandLeaseId);
}
