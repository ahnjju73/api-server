package helmet.bikelab.apiserver.services.endusers;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import helmet.bikelab.apiserver.workers.RiderWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class RiderDemandLeaseService extends SessService {

    private final RiderWorker riderWorker;

    @Transactional
    public BikeSessionRequest approveDemandLease(BikeSessionRequest request){
        Map param = request.getParam();
        String riderId = (String) param.get("rider_id");
        String clientId = (String) param.get("client_id");
        riderWorker.approveRiderDemandLease(riderId, clientId, request.getSessionUser());
        return request;
    }

    @Transactional
    public BikeSessionRequest rejectDemandLease(BikeSessionRequest request){
        Map param = request.getParam();
        String riderId = (String) param.get("rider_id");
        String reason = (String) param.get("reason");
        riderWorker.rejectRiderDemandLease(riderId, reason);
        return request;
    }
}
