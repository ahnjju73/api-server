package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.EstimateAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateAttachmentRepository extends JpaRepository<EstimateAttachments, Long> {
    List<EstimateAttachments> findAllByEstimates_EstimateId(String estimateId);
    void deleteAllByEstimates_EstimateId(String estimateId);
    void deleteAllByEstimateNo(Long estimateNo);
}
