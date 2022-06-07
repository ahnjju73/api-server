package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.CommonCode;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.DiagramInfoRequest;
import helmet.bikelab.apiserver.repositories.DiagramsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DiagramService extends SessService {

    private final BikeWorker bikeWorker;
    private final DiagramsRepository diagramsRepository;

    @Transactional
    public BikeSessionRequest addNewDiagram(BikeSessionRequest request){
        DiagramInfoRequest diagramInfoRequest = map(request.getParam(), DiagramInfoRequest.class);
        diagramInfoRequest.checkValidation();
        bikeWorker.getCommonCodeBikesById(diagramInfoRequest.getCarModel());
        Diagrams diagrams = new Diagrams(diagramInfoRequest);
        diagramsRepository.save(diagrams);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateDiagram(BikeSessionRequest request){
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteDiagram(BikeSessionRequest request){
        return request;
    }

    public BikeSessionRequest fetchDiagramDetailsById(BikeSessionRequest request){
        return request;
    }

    public BikeSessionRequest fetchAllDiagramList(BikeSessionRequest request){
        return request;
    }

    public BikeSessionRequest generatePreSigned(BikeSessionRequest request){
        return request;
    }

    @Transactional
    public BikeSessionRequest updateImageByDiagramId(BikeSessionRequest request){
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteImageByDiagramId(BikeSessionRequest request){
        return request;
    }


}
