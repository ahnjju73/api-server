package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.bike.BikeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.objects.*;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.objects.responses.BikeInsuranceInfoResponse;
import helmet.bikelab.apiserver.objects.responses.BikeInsuranceListResponse;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.workers.BikeWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class BikesInsuranceService extends SessService {
    private final AutoKey autoKey;
    private final BikeWorker bikeWorker;
    private final BikeInsurancesRepository bikeInsurancesRepository;
    private final BikesRepository bikesRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    public SessionRequest getBikeInsuranceDetails(SessionRequest request){
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        BikeInsuranceInfoResponse bikeInsuranceInfoResponse = new BikeInsuranceInfoResponse(bikeInsurancesByNo);
        request.setResponse(bikeInsuranceInfoResponse);
        return request;
    }
    public SessionRequest getBikeInsurances(SessionRequest request){
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        Bikes bikeById = bikeWorker.getBikeById(bikeByIdRequest.getBikeId());
        BikeInsuranceListResponse bikeInsuranceListResponse = bikeWorker.getBikeInsuranceListByBikeId(bikeById);
        request.setResponse(bikeInsuranceListResponse);
        return request;
    }

    @Transactional
    public SessionRequest addNewBikeInsurance(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceInfo bikeInsuranceInfo = map(request.getParam(), BikeInsuranceInfo.class);
        bikeInsuranceInfo.checkValidation();
        Bikes bikeById = bikeWorker.getBikeById(bikeInsuranceInfo.getBikeId());
        String insuranceId = autoKey.makeGetKey("insurance");
        bikeInsuranceInfo.setInsuranceId(insuranceId);
        BikeInsurances bikeInsurances = new BikeInsurances(bikeInsuranceInfo, bikeById, insuranceId);
        bikeInsurances.setCreatedUserNo(sessionUser.getUserNo());
        bikeInsurances.setUpdatedUserNo(sessionUser.getUserNo());
        bikeInsurancesRepository.save(bikeInsurances);
        bikeById.setBikeInsuranceNo(bikeInsurances.getInsuranceNo());
        bikesRepository.save(bikeById);
        // todo: Bike History 에 추가하기
        String log = "<>신규 보험 생성 및 등록</>을 완료하였습니다. 증권번호 [<>" + insuranceId + "</>]";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_ADD, sessionUser.getUserNo(), bikeById.getBikeNo().toString(), log));
        return request;
    }

    @Transactional
    public SessionRequest updateBikeInsurance(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        BikeInsuranceInfo bikeInsuranceInfo = map(request.getParam(), BikeInsuranceInfo.class);
        bikeInsuranceInfo.checkValidation();
        bikeInsurancesByNo.updateBikeInsuranceInfo(bikeInsuranceInfo, sessionUser);
        bikeInsurancesRepository.save(bikeInsurancesByNo);
        return request;
    }

    @Transactional
    public SessionRequest deleteBikeInsurance(BikeSessionRequest request){
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        bikeInsurancesRepository.deleteByInsuranceNo(bikeInsurancesByNo.getInsuranceNo());
        return request;
    }

    @Transactional
    public SessionRequest renewBikeInsurance(BikeSessionRequest request){
        return request;
    }


    /**
     * Action Event
      * @param request
     * @return
     */
    @Transactional
    public SessionRequest setBikeInsuranceToBike(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        Bikes bikeByNo = bikeInsurancesByNo.getBike();
        bikeByNo.setBikeInsuranceNo(bikeInsurancesByNo.getInsuranceNo());
        bikesRepository.save(bikeByNo);
        // todo: Bike History 에 추가하기
        String log = "해당 차량에 보험등록을 완료하였습니다.\\n 보험 ID / 증권번호 [<>" + bikeInsurancesByNo.getInsuranceNo() + "</> / <>" + bikeInsurancesByNo.getInsuranceId() + "</>]";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_SET, sessionUser.getUserNo(), bikeByNo.getBikeNo().toString(), log));
        return request;
    }

    @Transactional
    public SessionRequest removeBikeInsuranceToBike(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        Bikes bikeByNo = bikeWorker.getBikeByNo(bikeInsurancesByNo.getBikeNo());
        bikeByNo.setBikeInsuranceNo(null);
        bikesRepository.save(bikeByNo);
        // todo: Bike History 에 추가하기
        String log = "해당 차량에 보험을 해지하였습니다.\\n 보험 ID / 증권번호 [<>" + bikeInsurancesByNo.getInsuranceNo() + "</> / <>" + bikeInsurancesByNo.getInsuranceId() + "</>]";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_REMOVE, sessionUser.getUserNo(), bikeByNo.getBikeNo().toString(), log));
        return request;
    }

    @Transactional
    public SessionRequest checkIfEnableBikeInsuranceToBike(BikeSessionRequest request){
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        Bikes bikeById = bikeWorker.getBikeById(bikeByIdRequest.getBikeId());
        BikeInsurances bikeInsurance = bikeById.getBikeInsurance();
        if(bePresent(bikeInsurance)) writeMessage("이미 사용중인 보험이 있습니다.");
        return request;
    }

    @Transactional
    public BikeSessionRequest payInsuranceFeeByNo(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        if(bePresent(bikeInsurancesByNo.getPaidFee()) && bikeInsurancesByNo.getPaidFee().equals(bikeInsurancesByNo.getFee()))
            writeMessage("이미 납부완료된 내역입니다.");
        bikeInsurancesByNo.setPaidFee(bikeInsurancesByNo.getFee(), sessionUser);
        bikeInsurancesRepository.save(bikeInsurancesByNo);
        return request;
    }

}
