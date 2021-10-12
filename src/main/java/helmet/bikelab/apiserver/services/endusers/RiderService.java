package helmet.bikelab.apiserver.services.endusers;

import helmet.bikelab.apiserver.domain.bike.BikeRidersBak;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.riders.Activities;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PageableResponse;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.fine.FetchFinesResponse;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.InsuranceDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.*;
import helmet.bikelab.apiserver.objects.bikelabs.release.ReleaseDto;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.BikeUserTodoService;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import helmet.bikelab.apiserver.workers.RiderWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class RiderService extends SessService {

    private final RiderWorker riderWorker;
    private final BikesRepository bikesRepository;
    private final BikeRiderBakRepository bikeRiderBakRepository;
    private final LeaseRepository leaseRepository;
    private final ActivitiesRepository activitiesRepository;
    private final CommonWorker commonWorker;

    @Transactional
    public BikeSessionRequest assignRiderToBike(BikeSessionRequest request){
        AssignRiderByBikeRequest riderBikeApproveRequest = map(request.getParam(), AssignRiderByBikeRequest.class);
        riderBikeApproveRequest.checkValidation();
        Bikes bikeByRiderIdAndBikeId = bikesRepository.findByBikeId(riderBikeApproveRequest.getBikeId());
        // todo: 리스 차량인지 검사해야함.
        Riders riderById = riderWorker.getRiderById(riderBikeApproveRequest.getRiderId());
        bikeByRiderIdAndBikeId.isRidable();
        bikeByRiderIdAndBikeId.assignRider(riderById, riderBikeApproveRequest.getStartAt(), riderBikeApproveRequest.getEndAt());
        bikesRepository.save(bikeByRiderIdAndBikeId);

        Activities activities = new Activities();
        activities.setRiderNo(riderById.getRiderNo());
        activities.setBikeNo(bikeByRiderIdAndBikeId.getBikeNo());
        activities.setActivityType(ActivityTypes.RIDER_ASSIGNED);
        activitiesRepository.save(activities);

        // todo: push to application for rider

        return request;
    }

    public BikeSessionRequest fetchRiders(BikeSessionRequest request){
        RiderListRequest riderListRequest = map(request.getParam(), RiderListRequest.class);
        ResponseListDto riders = commonWorker.fetchItemListByNextToken(riderListRequest, "bikelabs.riders.fetchRiders", "bikelabs.riders.countAllRiders", "rider_id");
        request.setResponse(riders);
        return request;
    }

    public BikeSessionRequest fetchRiderListByBike(BikeSessionRequest request){
        List list = getList("bikelabs.riders.fetchRiderListByBike", request.getParam());
        request.setResponse(list);
        return request;
    }

    public BikeSessionRequest fetchRiderBikeRequests(BikeSessionRequest request){
        List list = getList("bikelabs.riders.fetchRiderBikeRequests", request.getParam());
        request.setResponse(list);
        return request;
    }

    @Transactional
    public BikeSessionRequest doApproveRider(BikeSessionRequest request){
        RiderBikeApproveRequest riderBikeApproveRequest = map(request.getParam(), RiderBikeApproveRequest.class);
        riderBikeApproveRequest.checkValidation();
        Bikes bikeByRiderIdAndBikeId = riderWorker.getBikeByRiderIdAndBikeId(riderBikeApproveRequest.getRiderId(), riderBikeApproveRequest.getBikeId());
        if(!RiderStatusTypes.PENDING.equals(bikeByRiderIdAndBikeId.getRiderStatus())) withException("2006-002");
        bikeByRiderIdAndBikeId.doApproveRider();
        bikesRepository.save(bikeByRiderIdAndBikeId);
        Riders riders = bikeByRiderIdAndBikeId.getRiders();

        Activities activities = new Activities();
        activities.setRiderNo(riders.getRiderNo());
        activities.setBikeNo(bikeByRiderIdAndBikeId.getBikeNo());
        activities.setActivityType(ActivityTypes.RIDER_ASSIGNED);
        activitiesRepository.save(activities);

        return request;
    }

    @Transactional
    public BikeSessionRequest doDeclineRider(BikeSessionRequest request){
        RiderBikeApproveRequest riderBikeApproveRequest = map(request.getParam(), RiderBikeApproveRequest.class);
        riderBikeApproveRequest.checkValidation();

        Bikes bikeByRiderIdAndBikeId = riderWorker.getBikeByRiderIdAndBikeId(riderBikeApproveRequest.getRiderId(), riderBikeApproveRequest.getBikeId());
        Leases leases = leaseRepository.findByBike_BikeId(bikeByRiderIdAndBikeId.getBikeId());
        if(!BikeRiderStatusTypes.PENDING.equals(bikeByRiderIdAndBikeId.getRiderStatus())) withException("2006-002");

        BikeRidersBak bikeRidersBak = new BikeRidersBak();
        bikeRidersBak.setBikeNo(bikeByRiderIdAndBikeId.getBikeNo());
        bikeRidersBak.setRiderNo(bikeByRiderIdAndBikeId.getRiderNo());
        bikeRidersBak.setRiderStartAt(bikeByRiderIdAndBikeId.getRiderStartAt());
        bikeRidersBak.setRiderEndAt(bikeByRiderIdAndBikeId.getRiderEndAt());
        bikeRidersBak.setRiderRequestAt(bikeByRiderIdAndBikeId.getRiderRequestAt());
        bikeRiderBakRepository.save(bikeRidersBak);

        Activities activities = new Activities();
        activities.setRiderNo(bikeByRiderIdAndBikeId.getRiderNo());
        activities.setActivityType(ActivityTypes.RIDER_REQUEST_DECLINE);
        activities.setBikeNo(bikeByRiderIdAndBikeId.getBikeNo());
        if(bePresent(leases)){
            activities.setClientNo(leases.getClientNo());
        }
        activitiesRepository.save(activities);

        bikeByRiderIdAndBikeId.doDeclineRider();
        bikesRepository.save(bikeByRiderIdAndBikeId);

        return request;
    }

}
