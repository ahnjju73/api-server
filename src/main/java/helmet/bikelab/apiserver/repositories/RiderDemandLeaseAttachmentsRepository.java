package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderDemandLeaseAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiderDemandLeaseAttachmentsRepository extends JpaRepository<RiderDemandLeaseAttachments, Integer> {
    void deleteByDemandLeaseFileNoAndRiderNo(Long fileNo, Integer riderNo);
    void deleteAllByRiderNo(Integer riderNo);
    List<RiderDemandLeaseAttachments> findAllByRiderNo(Integer riderNo);
}
