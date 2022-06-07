package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiagramPartsService extends SessService {


    @Transactional
    public BikeSessionRequest addPartsByDiagramId(BikeSessionRequest request){
        return request;
    }

    @Transactional
    public BikeSessionRequest removePartsByDiagramId(BikeSessionRequest request){
        return request;
    }

    public BikeSessionRequest fetchPartListByDiagramId(BikeSessionRequest request){
        return request;
    }

    public BikeSessionRequest fetchAllPartListOfDiagramId(BikeSessionRequest request){
        return request;
    }
}
