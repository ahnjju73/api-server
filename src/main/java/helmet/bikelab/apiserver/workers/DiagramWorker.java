package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.CommonWorking;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagramWorker extends Workspace {

    private final DiagramsRepository diagramsRepository;

    public Diagrams getDiagramById(String diagramId){
        Diagrams byDiagramId = diagramsRepository.findByDiagramId(diagramId);
        if(!bePresent(byDiagramId)) withException("550-001");
        return byDiagramId;
    }

}

