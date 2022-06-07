package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiagramService extends SessService {

    @Transactional
    public BikeSessionRequest addNewDiagram(BikeSessionRequest request){
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
