package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.DiagramInfoRequest;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiagramService extends SessService {

    @Transactional
    public BikeSessionRequest addNewDiagram(BikeSessionRequest request){
        DiagramInfoRequest diagramInfoRequest = map(request.getParam(), DiagramInfoRequest.class);
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
