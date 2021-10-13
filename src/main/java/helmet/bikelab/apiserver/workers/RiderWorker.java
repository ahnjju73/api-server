package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.riders.*;
import helmet.bikelab.apiserver.domain.types.AccountTypes;
import helmet.bikelab.apiserver.domain.types.ActivityTypes;
import helmet.bikelab.apiserver.domain.types.RiderStatusTypes;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import helmet.bikelab.apiserver.objects.requests.AddUpdateRiderRequest;
import helmet.bikelab.apiserver.objects.responses.FetchRiderDetailResponse;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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

    public FetchRiderDetailResponse getRiderDetail(String riderId){
        FetchRiderDetailResponse fetchRiderDetailResponse = new FetchRiderDetailResponse();
        Riders rider = riderRepository.findByRiderId(riderId);
        RiderInfo riderInfo = rider.getRiderInfo();
        fetchRiderDetailResponse.setRiderId(riderId);
        fetchRiderDetailResponse.setRiderNo(rider.getRiderNo());
        fetchRiderDetailResponse.setCreatedAt(rider.getCreatedAt());

        RiderInfoDto riderInfoDto = new RiderInfoDto();
        riderInfoDto.setRiderEmail(rider.getEmail());
        riderInfoDto.setRiderName(riderInfo.getName());
        riderInfoDto.setRiderStatus(rider.getStatus().getRiderStatusType());
        riderInfoDto.setRiderPhone(rider.getPhone());
        fetchRiderDetailResponse.setRiderInfo(riderInfoDto);

        List<BikeDto> leasingBikes = new ArrayList<>();
        List<Bikes> allByRiderNo = bikesRepository.findAllByRiderNo(rider.getRiderNo());
        for(Bikes bike : allByRiderNo){
            BikeDto bikeDto = new BikeDto();
            bikeDto.setBikeId(bike.getBikeId());
            bikeDto.setBikeNum(bike.getCarNum());
            bikeDto.setVimNum(bike.getVimNum());
            bikeDto.setBikeModel(bike.getCarModelCode());
            bikeDto.setBikeVolume(bike.getCarModel().getVolume());
            bikeDto.setBikeType(bike.getCarModel().getBikeType().getType());
            leasingBikes.add(bikeDto);
        }
        fetchRiderDetailResponse.setLeasingBikes(leasingBikes);
        return fetchRiderDetailResponse;
    }

    public void updateRider(AddUpdateRiderRequest addUpdateRiderRequest){
        addUpdateRiderRequest.checkValidation();
        Riders riders = riderRepository.findByRiderId(addUpdateRiderRequest.getRiderId());
        if(!bePresent(riders))
            withException("950-004");
        riders.setEmail(addUpdateRiderRequest.getEmail());
        riders.setPhone(addUpdateRiderRequest.getPhone());
        riderRepository.save(riders);

        RiderInfo riderInfo = new RiderInfo();
        riderInfo.setRiderNo(riders.getRiderNo());
        riderInfo.setRider(riders);
        riderInfo.setName(addUpdateRiderRequest.getName());
        riderInfoRepository.save(riderInfo);
    }

    public void stopRider(String riderId){
        Riders rider = riderRepository.findByRiderId(riderId);
        rider.setStatus(RiderStatusTypes.DEACTIVATE);
        riderRepository.save(rider);
    }

    public String resetPassword(String riderId){
        Riders rider = riderRepository.findByRiderId(riderId);
        if(rider.getStatus() != RiderStatusTypes.ACTIVATE)
            withException("950-005");
        RiderPassword riderPassword = rider.getRiderPassword();
        Random random = new Random();
        char[] word = new char[8];
        for(int j = 0; j < word.length; j++)
        {
            word[j] = (char)('a' + random.nextInt(26));
        }
        String randomPassword = new String(word);
        riderPassword.newPassword(randomPassword);
        return randomPassword;
    }

}
