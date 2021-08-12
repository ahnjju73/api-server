package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.BikeModelByIdRequest;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.BikeWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BikeModelService extends SessService {

    private final BikeWorker bikeWorker;

    public BikeSessionRequest fetchModelManufacturer(BikeSessionRequest request){
        List<Manufacturers> response = bikeWorker.getManufacturers();
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchModelManufacturerByCode(BikeSessionRequest request){
        BikeModelByIdRequest bikeModelByIdRequest = map(request.getParam(), BikeModelByIdRequest.class);
        CommonCodeBikes commonCodeBikesById = bikeWorker.getCommonCodeBikesById(bikeModelByIdRequest.getCode());
        request.setResponse(commonCodeBikesById);
        return request;
    }

}
