package helmet.bikelab.apiserver.repositories;


import helmet.bikelab.apiserver.domain.EstimateParts;
import helmet.bikelab.apiserver.domain.types.EstimateTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.objects.EstimatePartsPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstimatePartsRepository extends JpaRepository<EstimateParts, EstimatePartsPK> {
    List<EstimateParts> findAllByEstimate_EstimateId(String estimateId);
    List<EstimateParts> findAllByEstimate_EstimateIdAndIsFreeSupportAndEstimateType(String estimateId, YesNoTypes isFreeSupport, EstimateTypes estimateTypes);
    Integer countAllByEstimate_EstimateIdAndEstimateType(String estimateId, EstimateTypes estimateTypes);
    List<EstimateParts> findAllByEstimate_EstimateIdAndEstimateType(String estimateId, EstimateTypes estimateTypes);
    void deleteAllByEstimate_EstimateIdAndEstimateType(String estimateId, EstimateTypes estimateTypes);
    void deleteAllByEstimate_EstimateId(String estimateId);
    EstimateParts findByPartsNameAndPartsTypeNameAndPartsNoAndEstimateType(String partName, String partTypeName, Long partNo, EstimateTypes estimateTypes);
    EstimateParts findByPartsNoAndEstimate_EstimateIdAndEstimateType(Long partNo, String estimateId, EstimateTypes estimateTypes);
    List<EstimateParts> findAllByDiagramParts_DiagramNo(Integer diagramNo);
}
