package helmet.bikelab.apiserver.workers;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagramWorker extends Workspace {

    private final DiagramsRepository diagramsRepository;
    private final EstimatePartsRepository estimatePartsRepository;

    public Diagrams getDiagramById(String diagramId){
        Diagrams byDiagramId = diagramsRepository.findByDiagramId(diagramId);
        if(!bePresent(byDiagramId)) withException("550-001");
        return byDiagramId;
    }

}

