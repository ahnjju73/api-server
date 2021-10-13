package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.riders.*;
import helmet.bikelab.apiserver.domain.types.AccountTypes;
import helmet.bikelab.apiserver.domain.types.ActivityTypes;
import helmet.bikelab.apiserver.domain.types.RiderStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.AddUpdateRiderRequest;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiderWorker extends SessService {

    private final BikesRepository bikesRepository;
    private final BikeUserTodoRepository bikeUserTodoRepository;
    private final RiderRepository riderRepository;
    private final RiderInfoRepository riderInfoRepository;
    private final RiderPasswordRepository riderPasswordRepository;
    private final RiderAccountsRepository riderAccountsRepository;
    private final ActivitiesRepository activitiesRepository;
    private final AutoKey autoKey;

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

    public void addNewRider(Map param){
        AddUpdateRiderRequest addUpdateRiderRequest = map(param, AddUpdateRiderRequest.class);
        addUpdateRiderRequest.checkValidation();
        String riderId = autoKey.makeGetKey("rider");
        Riders riders = new Riders();
        riders.setRiderId(riderId);
        riders.setCreatedAt(LocalDateTime.now());
        riders.setEmail(addUpdateRiderRequest.getEmail());
        riders.setPhone(addUpdateRiderRequest.getPhone());
        riders.setStatus(RiderStatusTypes.ACTIVATE);
        riderRepository.save(riders);

        RiderInfo riderInfo = new RiderInfo();
        riderInfo.setRiderNo(riders.getRiderNo());
        riderInfo.setRider(riders);
        riderInfo.setName(addUpdateRiderRequest.getName());

        RiderPassword riderPassword = new RiderPassword();
        riderPassword.setRider(riders);
        riderPassword.setRiderNo(riders.getRiderNo());
        riderPassword.newPassword(addUpdateRiderRequest.getEmail());

        RiderAccounts riderAccount = new RiderAccounts();
        riderAccount.setRider(riders);
        riderAccount.setRiderNo(riders.getRiderNo());
        riderAccount.setAccountType(AccountTypes.EMAIL);

        riderInfoRepository.save(riderInfo);
        riderPasswordRepository.save(riderPassword);
        riderAccountsRepository.save(riderAccount);

        Activities activities = new Activities();
        activities.setActivityType(ActivityTypes.RIDER_SIGN_UP);
        activities.setRiderNo(riders.getRiderNo());
        activitiesRepository.save(activities);

    }

}
