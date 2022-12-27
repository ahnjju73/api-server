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
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.workers.BikeWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        Bikes bike = bikeInsurancesByNo.getBike();
        Map param = new HashMap();
        param.put("bike_no", bike.getBikeNo());
        Integer count = (Integer)getItem("bikelabs.insurance.checkIsTransferred", param);
        BikeInsuranceInfoResponse bikeInsuranceInfoResponse = new BikeInsuranceInfoResponse(bikeInsurancesByNo);
        bikeInsuranceInfoResponse.setIsTransferred(bePresent(count) && count > 0 ? true : false);
        request.setResponse(bikeInsuranceInfoResponse);
        return request;
    }
    public SessionRequest getBikeInsurances(SessionRequest request){
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        Bikes bikeById = bikeWorker.getBikeById(bikeByIdRequest.getBikeId());
        Map param = new HashMap();
        param.put("bike_no", bikeById.getBikeNo());
        Integer count = (Integer)getItem("bikelabs.insurance.checkIsTransferred", param);
        BikeInsuranceListResponse bikeInsuranceListResponse = bikeWorker.getBikeInsuranceListByBikeId(bikeById);
        bikeInsuranceListResponse.setIsTransferred(bePresent(count) && count > 0 ? true : false);
        request.setResponse(bikeInsuranceListResponse);
        return request;
    }

    @Transactional
    public SessionRequest addNewBikeInsurance(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceInfo bikeInsuranceInfo = map(request.getParam(), BikeInsuranceInfo.class);
        bikeInsuranceInfo.checkValidation();
        Bikes bikeById = bikeWorker.getBikeById(bikeInsuranceInfo.getBikeId());
        BikeInsurances selectedInsurance = bikeById.getBikeInsurance();
        if(bePresent(selectedInsurance)){
            writeMessage("차량보험은 최대 1개까지 등록이 가능합니다.");
        }
        String insuranceId = autoKey.makeGetKey("insurance");
        bikeInsuranceInfo.setInsuranceId(insuranceId);
        BikeInsurances bikeInsurances = new BikeInsurances(bikeInsuranceInfo, bikeById, insuranceId);
        bikeInsurances.setCreatedUserNo(sessionUser.getUserNo());
        bikeInsurances.setUpdatedUserNo(sessionUser.getUserNo());
        bikeInsurancesRepository.save(bikeInsurances);
        bikeById.setBikeInsuranceNo(bikeInsurances.getInsuranceNo());
        bikesRepository.save(bikeById);
        // todo: Bike History 에 추가하기
        String log = "<>신규 보험 생성 및 등록을 완료하였습니다.</>\\n 증권번호 [<>" + insuranceId + "</>]";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_ADD, sessionUser.getUserNo(), bikeById.getBikeNo().toString(), log));
        return request;
    }

    @Transactional
    public SessionRequest updateBikeInsurance(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        Bikes bike = bikeInsurancesByNo.getBike();
        checkIsTransferred(bike);
        BikeInsuranceInfo bikeInsuranceInfo = map(request.getParam(), BikeInsuranceInfo.class);
        bikeInsuranceInfo.checkValidation();
        bikeInsurancesByNo.updateBikeInsuranceInfo(bikeInsuranceInfo, sessionUser);
        bikeInsurancesRepository.save(bikeInsurancesByNo);
        return request;
    }

    @Transactional
    public SessionRequest deleteBikeInsurance(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        Bikes bike = bikeInsurancesByNo.getBike();
        checkIsTransferred(bike);
        BikeInsurances usingBikeInsurance = bike.getBikeInsurance();
        if(bePresent(usingBikeInsurance) && usingBikeInsurance.getInsuranceNo().equals(bikeInsurancesByNo.getInsuranceNo())){
            writeMessage("현재 사용중인 보험은 삭제할수 없습니다.\n사용해지후 삭제해주세요.");
        }
        String log = getDeleteBikeInsuranceLog(bikeInsurancesByNo, sessionUser);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_DELETE, sessionUser.getUserNo(), bike.getBikeNo().toString(), log));
        bikeInsurancesRepository.deleteByInsuranceNo(bikeInsurancesByNo.getInsuranceNo());
        return request;
    }

    public String getDeleteBikeInsuranceLog(BikeInsurances bikeInsurance, BikeUser user){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        StringBuilder log = new StringBuilder("");
        log.append("<>차량 보험[" + bikeInsurance.getInsuranceId() + (bePresent(bikeInsurance.getStockNumber()) ? (
                "/" + bikeInsurance.getStockNumber()) : ""
        ) + "]을 삭제하였습니다.</>\\n");
        if(bePresent(bikeInsurance.getStartAt())){
            String date = bikeInsurance.getStartAt().format(dateTimeFormatter);
            log.append("<>시작일(부터) : " + date + "\\n");
        }
        if(bePresent(bikeInsurance.getEndAt())){
            String date = bikeInsurance.getEndAt().format(dateTimeFormatter);
            log.append("<>시작일(까지)</> : " + date + "\\n");
        }
        if(bePresent(bikeInsurance.getStockNumber())){
            log.append("<>증권번호</> : " + bikeInsurance.getStockNumber() + "\\n");
        }
        if(bePresent(bikeInsurance.getFee())){
            log.append("<>보험료</> : " + Utils.getCurrencyFormat(bikeInsurance.getFee()) + "원");
            log.append(bikeInsurance.isPaid() ? " <>[납부완료]</>" : "<>[미납]</>");
            log.append("\\n");
        }
        if(bePresent(bikeInsurance.getBikeInsuranceType())){
            log.append("<>보험용도</> : " + bikeInsurance.getBikeInsuranceType().getTypeName() + "\\n");
        }
        if(bePresent(bikeInsurance.getType())){
            log.append("<>보험형태</> : " + bikeInsurance.getType().getTypeName() + "\\n");
        }
        if(bePresent(bikeInsurance.getCompanyName())){
            log.append("<>보험사</> : " + bikeInsurance.getCompanyName() + "\\n");
        }
        if(bePresent(bikeInsurance.getAge())){
            log.append("<>나이</> : " + bikeInsurance.getAge() + "세\\n");
        }
        if(bePresent(bikeInsurance.getLiabilityMan())){
            log.append("<>대인</> : " + Utils.getCurrencyFormat(bikeInsurance.getLiabilityMan()) + "원\\n");
        }
        if(bePresent(bikeInsurance.getLiabilityCar())){
            log.append("<>대물</> : " + Utils.getCurrencyFormat(bikeInsurance.getLiabilityCar()) + "원\\n");
        }
        if(bePresent(bikeInsurance.getLiabilityMan2())){
            log.append("<>대인2</> : " + (bikeInsurance.getLiabilityMan2().equals(-1) ? "무한(종합)" : "0원(책임)") + "\\n");
        }
        if(bePresent(bikeInsurance.getSelfCoverMan())){
            log.append("<>자손</> : " + Utils.getCurrencyFormat(bikeInsurance.getSelfCoverMan()) + "원\\n");
        }
        if(bePresent(bikeInsurance.getSelfCoverCar())){
            log.append("<>자차</> : " + bikeInsurance.getSelfCoverCar().getCoverTypeName() + "\\n");
        }
        if(bePresent(bikeInsurance.getNoInsuranceCover())){
            log.append("<>무보험차 상해</> : " + Utils.getCurrencyFormat(bikeInsurance.getNoInsuranceCover()) + "원\\n");
        }
        return log.toString();
    }

    private void checkIsTransferred(Bikes bike){
        Map param = new HashMap();
        param.put("bike_no", bike.getBikeNo());
        Integer count = (Integer)getItem("bikelabs.insurance.checkIsTransferred", param);
        if(bePresent(count) && count > 0) writeMessage("차량보험 이전이력이 있는 차량은 사용이 불가능합니다.");
    }

    public String logSetBikeInsuranceToBike(Bikes bike, BikeInsurances bikeInsurance){
        BikeInsurances prevInsurance = bike.getBikeInsurance();
        StringBuilder log = new StringBuilder("");
        if(!bePresent(prevInsurance)){
            log.append("<>해당 차량에 보험등록을 완료하였습니다.</>\\n 보험 ID / 증권번호 [<>" + bikeInsurance.getInsuranceNo() + "</> / <>" + bikeInsurance.getInsuranceId() + "</>]");
        }else {
            log.append("<>차량의 보험정보를 변경하였습니다.</>\\n");
            log.append("<>이전 보험의 증권번호</> : [" + prevInsurance.getInsuranceId() + "] " + prevInsurance.getStockNumber() + "\\n");
            log.append("<>변경 보험의 증권번호</> : [" + bikeInsurance.getInsuranceId() + "] " + bikeInsurance.getStockNumber() + "\\n");
        }
        return log.toString();
    }

    public void logTransBikeInsuranceToBike(Bikes prevBike, BikeInsurances prevBikeInsurance, Bikes toBike, BikeInsurances toBikeInsurance, BikeUser user){
        StringBuilder toLog = new StringBuilder("");
        toLog.append("<>차량 보험정보를 이관하였습니다.</>\\n");
        toLog.append("to 차대번호 : " + (bePresent(toBike.getVimNum()) ? toBike.getVimNum() : "-") + "\\n");
        toLog.append("to 차량번호 : " + (bePresent(toBike.getCarNum()) ? toBike.getCarNum() : "-") + "\\n");
        toLog.append("보험 ID : ");
        toLog.append(prevBikeInsurance.getInsuranceId() + " [" + (prevBikeInsurance.getInsuranceNo()) + "]");
        toLog.append(" -> ");
        toLog.append(toBikeInsurance.getInsuranceId() + " [" + (toBikeInsurance.getInsuranceNo()) + "]");
        toLog.append("\\n");
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_TRANSFER, user.getUserNo(), prevBike.getBikeNo().toString(), toLog.toString()));

        StringBuilder fromLog = new StringBuilder("");
        fromLog.append("<>차량 보험정보가 이관되었습니다.</>\\n");
        fromLog.append("from 차대번호 : " + (bePresent(prevBike.getVimNum()) ? prevBike.getVimNum() : "-") + "\\n");
        fromLog.append("from 차량번호 : " + (bePresent(prevBike.getCarNum()) ? prevBike.getCarNum() : "-") + "\\n");
        fromLog.append("보험 ID : ");
        fromLog.append(prevBikeInsurance.getInsuranceId() + " [" + (prevBikeInsurance.getInsuranceNo()) + "]");
        fromLog.append(" -> ");
        fromLog.append(toBikeInsurance.getInsuranceId() + " [" + (toBikeInsurance.getInsuranceNo()) + "]");
        fromLog.append("\\n");
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_TRANSFER, user.getUserNo(), toBike.getBikeNo().toString(), fromLog.toString()));
    }

    @Transactional
    public SessionRequest removeBikeInsuranceToBike(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        Bikes bikeByNo = bikeWorker.getBikeByNo(bikeInsurancesByNo.getBikeNo());
        checkIsTransferred(bikeByNo);
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
        Bikes bike = bikeInsurancesByNo.getBike();
        String log = "<>차량보험료를 납부하였습니다.</> [" + bikeInsurancesByNo.getInsuranceId() + " / " + bikeInsurancesByNo.getInsuranceNo() + "]";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_SET, sessionUser.getUserNo(), bike.getBikeNo().toString(), log));
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
        if(!bePresent(bikeInsurancesByNo.getStockNumber())) writeMessage("증권번호가 없습니다.");
        Bikes bikeByNo = bikeInsurancesByNo.getBike();
        checkIsTransferred(bikeByNo);

        bikeByNo.setBikeInsuranceNo(bikeInsurancesByNo.getInsuranceNo());
        bikesRepository.save(bikeByNo);
        String log = logSetBikeInsuranceToBike(bikeByNo, bikeInsurancesByNo);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.BIKE_INSURANCE_SET, sessionUser.getUserNo(), bikeByNo.getBikeNo().toString(), log));
        return request;
    }

    @Transactional
    public BikeSessionRequest transferBikeInsuranceToAnotherBike(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        Bikes selectedBike = bikeInsurancesByNo.getBike();
        checkIsTransferred(selectedBike);

        Bikes nextBike = bikeWorker.getBikeById(bikeByIdRequest.getBikeId());
        if(bePresent(nextBike.getBikeInsurance())){
            writeMessage("이미 보험정보가 존재합니다.");
        }
        if(selectedBike.getBikeNo().equals(nextBike.getBikeNo())) writeMessage("같은 차량으로 보험이전을 할 수 없습니다.");

        bikeInsurancesByNo.setIsTransferred(true);
        bikeInsurancesByNo.setUpdatedAt(LocalDateTime.now());
        bikeInsurancesByNo.setUpdatedUserNo(sessionUser.getUserNo());
        bikeInsurancesRepository.save(bikeInsurancesByNo);

        BikeInsurances transBikeInsurance = new BikeInsurances();
        transBikeInsurance.transferBikeInsuranceTo(bikeInsurancesByNo, nextBike, sessionUser);
        bikeInsurancesRepository.save(transBikeInsurance);

        nextBike.setBikeInsuranceNo(transBikeInsurance.getInsuranceNo());
        bikesRepository.save(nextBike);

        selectedBike.setBikeInsuranceNo(null);
        bikesRepository.save(selectedBike);

        logTransBikeInsuranceToBike(selectedBike, bikeInsurancesByNo, nextBike, transBikeInsurance, sessionUser);

        return request;
    }

    @Transactional
    public SessionRequest renewBikeInsurance(BikeSessionRequest request){
        BikeUser sessionUser = request.getSessionUser();
        BikeInsuranceByNoRequest bikeInsuranceByNoRequest = map(request.getParam(), BikeInsuranceByNoRequest.class);
        BikeInsurances bikeInsurancesByNo = bikeWorker.getBikeInsurancesByNo(bikeInsuranceByNoRequest.getInsuranceNo());
        Bikes bike = bikeInsurancesByNo.getBike();
        checkIsTransferred(bike);
        BikeInsurances renewInsurance = new BikeInsurances();
        renewInsurance.renewBikeInsurance(bikeInsurancesByNo, sessionUser);
        bikeInsurancesRepository.save(renewInsurance);
        Map response = new HashMap();
        response.put("insurance_no", renewInsurance.getInsuranceNo());
        request.setResponse(response);
        return request;
    }

}
