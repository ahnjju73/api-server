package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.repositories.BikeUserTodoRepository;
import helmet.bikelab.apiserver.repositories.BikesRepository;
import helmet.bikelab.apiserver.repositories.RiderRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RiderWorker extends SessService {

    private final BikesRepository bikesRepository;
    private final BikeUserTodoRepository bikeUserTodoRepository;
    private final RiderRepository riderRepository;

    public Bikes getBikeByRiderIdAndBikeId(String riderId, String bikeId){
        Bikes byBikeIdAndRiders_riderId = bikesRepository.findByBikeIdAndRiders_RiderId(bikeId, riderId);
        if(!bePresent(byBikeIdAndRiders_riderId)) withException("2006-001");
        return byBikeIdAndRiders_riderId;
    }

    public Riders getRiderById(String riderId){
        Riders byRiderId = riderRepository.findByRiderId(riderId);
        if(!bePresent(byRiderId)) withException("510-001");
        return byRiderId;
    }

}
