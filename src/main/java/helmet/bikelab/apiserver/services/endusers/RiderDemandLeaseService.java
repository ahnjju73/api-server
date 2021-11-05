package helmet.bikelab.apiserver.services.endusers;

import helmet.bikelab.apiserver.domain.riders.Activities;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.ActivityTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.PushComponent;
import helmet.bikelab.apiserver.utils.Senders;
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
    private final PushComponent pushComponent;
    private final ActivitiesRepository activitiesRepository;

    @Transactional
    public BikeSessionRequest approveDemandLease(BikeSessionRequest request){
        Map param = request.getParam();
        String riderId = (String) param.get("rider_id");
        String clientId = (String) param.get("client_id");
        riderWorker.approveRiderDemandLease(riderId, clientId, request.getSessionUser());

        // todo: 메세지 보내기 (push)
        Riders riderById = riderWorker.getRiderById(riderId);
        Activities activities = new Activities();
        activities.setRiderNo(riderById.getRiderNo());
        activities.setActivityType(ActivityTypes.RIDER_DEMAND_LEASE_CHECKED);
        activitiesRepository.save(activities);
        if(bePresent(riderById.getNotificationToken())) pushComponent.pushNotification(riderById.getNotificationToken(), "리스신청서를 검토중입니다.", "요청하신 리스신청서를 관리자가 계약을 진행하고 있습니다.");
        return request;
    }

    @Transactional
    public BikeSessionRequest rejectDemandLease(BikeSessionRequest request){
        Map param = request.getParam();
        String riderId = (String) param.get("rider_id");
        String reason = (String) param.get("reason");
        riderWorker.rejectRiderDemandLease(riderId, reason);

        Riders riderById = riderWorker.getRiderById(riderId);
        Activities activities = new Activities();
        activities.setRiderNo(riderById.getRiderNo());
        activities.setActivityType(ActivityTypes.RIDER_DEMAND_LEASE_REJECTED);
        activitiesRepository.save(activities);
        if(bePresent(riderById.getNotificationToken())) pushComponent.pushNotification(riderById.getNotificationToken(), "리스신청서가 반려되었습니다.", "요청하신 리스신청서를 관리자가 반려하였습니다. 혹시 문의가 필요하다면 '설정 > 1:1문의' 또는 '상담센터'로 연락주시기 바랍니다.");
        return request;
    }
}
