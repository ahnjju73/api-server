package helmet.bikelab.apiserver.services.endusers;

import com.amazonaws.services.dynamodbv2.xspec.B;
import helmet.bikelab.apiserver.domain.bike.BikeRidersBak;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.riders.Activities;
import helmet.bikelab.apiserver.domain.riders.InquiryRiderInsurances;
import helmet.bikelab.apiserver.domain.riders.RiderVerified;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.*;
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
import helmet.bikelab.apiserver.utils.PushComponent;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import helmet.bikelab.apiserver.workers.RiderWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final LeasesWorker leasesWorker;
    private final InquiryRiderInsurancesRepository inquiryRiderInsurancesRepository;
    private final RiderVerifiedRepository riderVerifiedRepository;
    private final RiderRepository riderRepository;
    private final PushComponent pushComponent;

    @Transactional
    public BikeSessionRequest doRejectRiderVerified(BikeSessionRequest request){
        Map param = request.getParam();
        String riderId = (String)param.get("rider_id");
        String message = (String)param.get("message");
        Riders riderById = riderWorker.getRiderById(riderId);
        riderById.setVerifiedRejectMessage(message);
        riderVerifiedRepository.deleteAllByRiderNoAndRequestType(riderById.getRiderNo(), RiderVerifiedRequestTypes.REQUEST);
        riderById.setVerifiedRequestAt(null);
        riderById.setVerifiedType(RiderVerifiedTypes.REJECTED);
        riderRepository.save(riderById);

        Activities activities = new Activities();
        activities.setRiderNo(riderById.getRiderNo());
        activities.setActivityType(ActivityTypes.RIDER_VERIFIED_REJECTED);
        activitiesRepository.save(activities);

        String returnMessage = "";
        if(bePresent(message)){
            returnMessage = "다음과 같은 이유로 인증이 완료되지 못했습니다." + "\n* " + message.substring(0, message.length() > 120 ? 120 : message.length()) + (message.length() > 120 ? "..." : "");
        }else {
            returnMessage = "자료가 부족하여서 인증에 실패하였습니다.";
        }
        pushComponent.pushNotification(riderById.getNotificationToken(), "인증이 완료되지못했습니다..", returnMessage);

        return request;
    }

    @Transactional
    public BikeSessionRequest doApproveRiderVerified(BikeSessionRequest request){
        Map param = request.getParam();
        String riderId = (String)param.get("rider_id");
        Riders riderById = riderWorker.getRiderById(riderId);
        riderVerifiedRepository.deleteAllByRiderNoAndRequestType(riderById.getRiderNo(), RiderVerifiedRequestTypes.VERIFIED);
        List<RiderVerified> allByRiderNoAndRequestType = riderVerifiedRepository.findAllByRiderNoAndRequestType(riderById.getRiderNo(), RiderVerifiedRequestTypes.REQUEST);
        if(!bePresent(allByRiderNoAndRequestType)) withException("960-001");
        allByRiderNoAndRequestType.forEach(e -> {
            e.setRequestType(RiderVerifiedRequestTypes.VERIFIED);
        });
        riderVerifiedRepository.saveAll(allByRiderNoAndRequestType);
        riderById.setVerifiedAt(LocalDateTime.now());
        riderById.setVerifiedRequestAt(null);
        riderById.setVerifiedType(RiderVerifiedTypes.VERIFIED);
        riderRepository.save(riderById);
        Activities activities = new Activities();
        activities.setRiderNo(riderById.getRiderNo());
        activities.setActivityType(ActivityTypes.RIDER_VERIFIED_COMPLETED);
        activitiesRepository.save(activities);

        pushComponent.pushNotification(riderById.getNotificationToken(), "인증이 완료되었습니다.", "요청하신 라이더 인증이 완료되어서 리스신청이 가능합니다!");

        return request;
    }

    public BikeSessionRequest fetchRiderVerified(BikeSessionRequest request){
        Map param = request.getParam();
        String riderId = (String)param.get("rider_id");
        Riders riderById = riderWorker.getRiderById(riderId);
        RiderVerifiedResponse response = new RiderVerifiedResponse();
        response.setVerified(riderById.getVerifiedType());
        response.setVerifiedAt(riderById.getVerifiedAt());
        response.setVerifiedRejectMessage(riderById.getVerifiedRejectMessage());
        response.setVerifiedRequestAt(riderById.getVerifiedRequestAt());
        List<RiderVerified> verifiedList = riderVerifiedRepository.findAllByRiderNoAndRequestType(riderById.getRiderNo(), RiderVerifiedRequestTypes.VERIFIED);
        List<RiderVerified> requestVerifiedList = riderVerifiedRepository.findAllByRiderNoAndRequestType(riderById.getRiderNo(), RiderVerifiedRequestTypes.REQUEST);
        response.setVerifiedList(verifiedList);
        response.setRequestVerifiedList(requestVerifiedList);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest assignRiderToBike(BikeSessionRequest request){
        AssignRiderByBikeRequest riderBikeApproveRequest = map(request.getParam(), AssignRiderByBikeRequest.class);
        riderBikeApproveRequest.checkValidation();
        Bikes bikeByRiderIdAndBikeId = bikesRepository.findByBikeId(riderBikeApproveRequest.getBikeId());
        bikeByRiderIdAndBikeId.isRidable();
        Riders riderById = riderWorker.getRiderById(riderBikeApproveRequest.getRiderId());
        Leases leaseByBikeNo = leaseRepository.findByBikeNo(bikeByRiderIdAndBikeId.getBikeNo());
        if(bePresent(leaseByBikeNo)){
            LeaseInfo leaseInfo = leaseByBikeNo.getLeaseInfo();
            LocalDateTime startAt = leaseInfo.getStart().atStartOfDay();
            LocalDateTime endAt = leaseInfo.getEndDate().atStartOfDay();
            if(ContractTypes.MANAGEMENT.equals(leaseByBikeNo.getContractTypes())){
                bikeByRiderIdAndBikeId.assignRider(riderById, startAt, endAt, leaseByBikeNo);
            }else {
                if(riderBikeApproveRequest.getStartAt().compareTo(startAt) < 0) withException("511-001");
                if(riderBikeApproveRequest.getStartAt().compareTo(endAt) > 0) withException("511-002");
                if(riderBikeApproveRequest.getEndAt().compareTo(startAt) < 0) withException("511-003");
                if(riderBikeApproveRequest.getEndAt().compareTo(endAt) > 0) withException("511-004");
                bikeByRiderIdAndBikeId.assignRider(riderById, riderBikeApproveRequest.getStartAt(), riderBikeApproveRequest.getEndAt(), leaseByBikeNo);
            }
        }else {
            bikeByRiderIdAndBikeId.assignRider(riderById, riderBikeApproveRequest.getStartAt(), riderBikeApproveRequest.getEndAt(), leaseByBikeNo);
        }
        bikesRepository.save(bikeByRiderIdAndBikeId);
        riderRepository.save(riderById);

        Activities activities = new Activities();
        activities.setRiderNo(riderById.getRiderNo());
        activities.setBikeNo(bikeByRiderIdAndBikeId.getBikeNo());
        activities.setActivityType(ActivityTypes.RIDER_ASSIGNED);
        activitiesRepository.save(activities);

        BikeRidersBak bikeRidersBak = new BikeRidersBak();
        bikeRidersBak.setBikeNo(bikeByRiderIdAndBikeId.getBikeNo());
        bikeRidersBak.setRiderNo(bikeByRiderIdAndBikeId.getRiderNo());
        bikeRidersBak.setRiderStartAt(bikeByRiderIdAndBikeId.getRiderStartAt());
        bikeRidersBak.setRiderEndAt(bikeByRiderIdAndBikeId.getRiderEndAt());
        bikeRidersBak.setRiderRequestAt(bikeByRiderIdAndBikeId.getRiderRequestAt());
        bikeRidersBak.setRiderLeaseNo(bikeByRiderIdAndBikeId.getRiderLeaseNo());
        bikeRiderBakRepository.save(bikeRidersBak);

        return request;
    }

    public BikeSessionRequest fetchRiders(BikeSessionRequest request){
        RiderListRequest riderListRequest = map(request.getParam(), RiderListRequest.class);
        ResponseListDto riders = commonWorker.fetchItemListByNextToken(riderListRequest, "bikelabs.riders.fetchRiders", "bikelabs.riders.countAllRiders", "rider_id");
        request.setResponse(riders);
        return request;
    }

    public BikeSessionRequest fetchRidersVerified(BikeSessionRequest request){
        RiderListRequest riderListRequest = map(request.getParam(), RiderListRequest.class);
        ResponseListDto riders = commonWorker.fetchItemListByNextToken(riderListRequest, "bikelabs.riders.fetchRidersVerified", "bikelabs.riders.countAllRidersVerified", "rider_id");
        request.setResponse(riders);
        return request;
    }

    public BikeSessionRequest fetchRidersLeaseRequested(BikeSessionRequest request){
        RiderListRequest riderListRequest = map(request.getParam(), RiderListRequest.class);
        ResponseListDto riders = commonWorker.fetchItemListByNextToken(riderListRequest, "bikelabs.riders.fetchRidersLeaseRequested", "bikelabs.riders.countAllRidersLeaseRequested", "rider_id");
        request.setResponse(riders);
        return request;
    }

    public BikeSessionRequest fetchRiderListByBike(BikeSessionRequest request){
        FetchRidersByBike fetchRidersByBike = map(request.getParam(), FetchRidersByBike.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(fetchRidersByBike, "bikelabs.riders.fetchRiderListByBike", "bikelabs.riders.countAllRiderListByBike", "bike_id");
        request.setResponse(responseListDto);
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

    @Transactional
    public BikeSessionRequest addNewRider(BikeSessionRequest request){
        riderWorker.addNewRider(request);
        return request;
    }

    public BikeSessionRequest fetchRiderDetail(BikeSessionRequest request){
        String riderId = (String) request.getParam().get("rider_id");
        request.setResponse(riderWorker.getRiderDetail(riderId));
        return request;
    }

    @Transactional
    public BikeSessionRequest updateRider(BikeSessionRequest request){
        AddUpdateRiderRequest addUpdateRiderRequest = map(request.getParam(), AddUpdateRiderRequest.class);
        riderWorker.updateRider(addUpdateRiderRequest);
        return request;
    }

    @Transactional
    public BikeSessionRequest changeRiderStatus(BikeSessionRequest request){
        String riderId = (String) request.getParam().get("rider_id");
        String status = (String) request.getParam().get("status");
        riderWorker.changeStatus(riderId, status);
        return request;
    }

    @Transactional
    public BikeSessionRequest resetPassword(BikeSessionRequest request){
        String riderId = (String) request.getParam().get("rider_id");
        String password = riderWorker.resetPassword(riderId);
        Map response = new HashMap();
        response.put("changed_password", password);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchRiderBikesHistory(BikeSessionRequest request){
        List<RiderBikeDto> riderBikes = riderWorker.getRiderBikes((String) request.getParam().get("rider_id"));
        request.setResponse(riderBikes);
        return request;
    }

    public BikeSessionRequest fetchRiderInsuranceDemandList(BikeSessionRequest request) {
        InsuranceInquiryRequest insuranceInquiryRequest = map(request.getParam(), InsuranceInquiryRequest.class);
        Pageable pageable = PageRequest.of(insuranceInquiryRequest.getPage(), insuranceInquiryRequest.getSize());
        if(bePresent(insuranceInquiryRequest.getStatus()) && bePresent(insuranceInquiryRequest.getPhone())){
            Page<InquiryRiderInsurances> allByPhoneContainingAndStatusOrderByInquiryNoDesc = inquiryRiderInsurancesRepository.findAllByPhoneContainingAndStatusOrderByInquiryNoDesc(insuranceInquiryRequest.getPhone(), InquiryStatusTypes.getInquiryStatusTypes(insuranceInquiryRequest.getStatus()), pageable);
            request.setResponse(allByPhoneContainingAndStatusOrderByInquiryNoDesc);
        }else if(bePresent(insuranceInquiryRequest.getPhone())) {
            Page<InquiryRiderInsurances> allByPhoneContainingOrderByInquiryNoDesc = inquiryRiderInsurancesRepository.findAllByPhoneContainingOrderByInquiryNoDesc(insuranceInquiryRequest.getPhone(), pageable);
            request.setResponse(allByPhoneContainingOrderByInquiryNoDesc);
        }else if(bePresent(insuranceInquiryRequest.getStatus())){
            Page<InquiryRiderInsurances> allByStatusOrderByInquiryNoDesc = inquiryRiderInsurancesRepository.findAllByStatusOrderByInquiryNoDesc(InquiryStatusTypes.getInquiryStatusTypes(insuranceInquiryRequest.getStatus()), pageable);
            request.setResponse(allByStatusOrderByInquiryNoDesc);
        }else{
            Page<InquiryRiderInsurances> all = inquiryRiderInsurancesRepository.findAllByOrderByInquiryNoDesc(pageable);
            request.setResponse(all);
        }
        return request;
    }
    public BikeSessionRequest fetchRiderInsuranceDemandDetail(BikeSessionRequest request) {
        String inquiryId = (String) request.getParam().get("inquiry_id");
        InquiryRiderInsurances byInquiryId = inquiryRiderInsurancesRepository.findByInquiryId(inquiryId);
        request.setResponse(byInquiryId);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateDescription(BikeSessionRequest request){
        UpdateConsultingDescriptionRequest descriptions = map(request.getParam(), UpdateConsultingDescriptionRequest.class);
        InquiryRiderInsurances byInquiryId = inquiryRiderInsurancesRepository.findByInquiryId(descriptions.getInquiryId());
        List<ConsultingDescriptionDto> descriptionList = byInquiryId.getDescriptions();
        if(bePresent(descriptionList)){
            byInquiryId.setDescriptions(descriptionList);
            if(bePresent(descriptions.getDescription())){
                ConsultingDescriptionDto consultingDescriptionDto = new ConsultingDescriptionDto();
                consultingDescriptionDto.setDescription(descriptions.getDescription());
                consultingDescriptionDto.setBikeUserNo(request.getSessionUser().getUserNo());
                consultingDescriptionDto.setBikeUserName(request.getSessionUser().getBikeUserInfo().getName());
                descriptionList.add(consultingDescriptionDto);
                byInquiryId.setDescriptions(descriptionList);
            }
        }else{
            List<ConsultingDescriptionDto> consultingDescriptionDtos = new ArrayList<>();
            ConsultingDescriptionDto consultingDescriptionDto = new ConsultingDescriptionDto();
            consultingDescriptionDto.setDescription(descriptions.getDescription());
            consultingDescriptionDto.setBikeUserNo(request.getSessionUser().getUserNo());
            consultingDescriptionDto.setBikeUserName(request.getSessionUser().getBikeUserInfo().getName());
            consultingDescriptionDtos.add(consultingDescriptionDto);
            byInquiryId.setDescriptions(consultingDescriptionDtos);
        }
        inquiryRiderInsurancesRepository.save(byInquiryId);
        return request;
    }

    @Transactional
    public BikeSessionRequest changeInsInqStatus(BikeSessionRequest request) {
        String inquiryId = (String) request.getParam().get("inquiry_id");
        InquiryRiderInsurances byInquiryId = inquiryRiderInsurancesRepository.findByInquiryId(inquiryId);
        byInquiryId.setStatus(InquiryStatusTypes.CONFIRMED);
        inquiryRiderInsurancesRepository.save(byInquiryId);
        return request;
    }
}
