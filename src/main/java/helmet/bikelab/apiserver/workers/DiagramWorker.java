package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.objects.requests.DiagramByIdRequest;
import helmet.bikelab.apiserver.objects.requests.DiagramInfoRequest;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagramWorker extends Workspace {

    private final DiagramsRepository diagramsRepository;

    public Diagrams getDiagramById(String diagramId){
        Diagrams byDiagramId = diagramsRepository.findByDiagramId(diagramId);
        if(!bePresent(byDiagramId)) withException("550-001");
        return byDiagramId;
    }

    public Diagrams setDiagramById(DiagramInfoRequest diagramInfoRequest, String diagramId){
        Diagrams byDiagramId = diagramsRepository.findByDiagramId(diagramId);
        byDiagramId.setCarModelCode(diagramInfoRequest.getCarModel());
        byDiagramId.setName(diagramInfoRequest.getName());
        // images 수정 추후 추가
        return byDiagramId;
    }

}

