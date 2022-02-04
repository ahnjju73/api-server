package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.EstimateHistories;
import helmet.bikelab.apiserver.domain.types.EstimateHistoryTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimateHistoriesRepository extends JpaRepository<EstimateHistories, Long> {
    void deleteAllByEstimate_EstimateId(String estimateId);
    List<EstimateHistories> findAllByEstimate_EstimateId(String estimateId);
    EstimateHistories findByEstimate_EstimateIdAndHistoryType(String estimateId, EstimateHistoryTypes estimateHistoryTypes);
}
