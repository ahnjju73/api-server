package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BikePartsService extends SessService {


    public BikeSessionRequest fetchParts(BikeSessionRequest request) {
        return request;
    }
}
