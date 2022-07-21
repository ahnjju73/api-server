package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeaseAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseAttachmentRepository extends JpaRepository<LeaseAttachments, Integer> {
    LeaseAttachments findByLease_LeaseId(String leaseId);
    void deleteAllByLease_LeaseId(String leaseId);

}
