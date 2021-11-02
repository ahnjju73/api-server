package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bike.BikeAttachments;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.lease.LeaseExpense;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.ExpenseTypes;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.CarModel;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasesDto;
import helmet.bikelab.apiserver.objects.requests.BikeListDto;
import helmet.bikelab.apiserver.objects.requests.BikeRequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class BikesService extends SessService {
    private final AutoKey autoKey;
    private final BikeAttachmentRepository bikeAttachmentRepository;
    private final BikesRepository bikesRepository;
    private final LeaseRepository leaseRepository;
    private final BikeModelsRepository bikeModelsRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final LeaseExpenseRepository expenseRepository;
    private final CommonWorker commonWorker;
    private final BikeWorker bikeWorker;

    public BikeSessionRequest fetchHistoriesByBikeId(BikeSessionRequest request){
        BikeListDto bikeListDto = map(request.getParam(), BikeListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(bikeListDto, "bikelabs.clients_log.fetchClientHistoriesByClientId", "bikelabs.clients_log.countAllClientHistoriesByClientId", "log_no");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchGroupDetailsByGroupId(BikeSessionRequest request){
        Map param = request.getParam();
        Map details = (Map)getItem("bikelabs.commons.clients.fetchGroupDetailsByGroupId", param);
        request.setResponse(details);
        return request;
    }

    public BikeSessionRequest fetchBikesRidable(BikeSessionRequest request){
        Map param = request.getParam();
        BikeRequestListDto requestListDto = map(param, BikeRequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.commons.bikes.fetchBikesRidable", "bikelabs.commons.bikes.countAllBikesRidable", "bike_id");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchBikesWithoutLease(BikeSessionRequest request){
        Map param = request.getParam();
        BikeRequestListDto requestListDto = map(param, BikeRequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.commons.bikes.fetchBikesListByNoLease", "bikelabs.commons.bikes.countAllBikeListByNoLease", "bike_id");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchBikes(BikeSessionRequest request){
        Map param = request.getParam();
        BikeRequestListDto requestListDto = map(param, BikeRequestListDto.class);
        ResponseListDto responseListDto;
        if(!bePresent(requestListDto.getSearchClientId())){
            responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.commons.bikes.fetchBikesList", "bikelabs.commons.bikes.countAllBikeList", "bike_id");
        }else {
            responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.commons.bikes.fetchBikesListByClientId", "bikelabs.commons.bikes.countAllBikeList", "bike_id");
        }

        request.setResponse(responseListDto);
        return request;
    }

    @Deprecated
    public BikeSessionRequest bak_fetchBikes(BikeSessionRequest request){
        List<Bikes> bikes = bikesRepository.findAll();
        List<FetchBikesResponse> fetchBikesResponses = new ArrayList<>();
        Map response = new HashMap();
        for(Bikes bike : bikes){
            FetchBikesResponse fetchBikesResponse = new FetchBikesResponse();
            CommonCodeBikes carModel = bike.getCarModel();
            fetchBikesResponse.setColor(bike.getColor());
            fetchBikesResponse.setNumber(bike.getCarNum());
            CarModel model = new CarModel();
            model.setCarModelCode(carModel.getCode());
            model.setCarModelName(carModel.getModel());
            fetchBikesResponse.setModel(model);
            fetchBikesResponse.setYears(bike.getYears());
            fetchBikesResponse.setVimNum(bike.getVimNum());
            fetchBikesResponse.setBikeId(bike.getBikeId());
            fetchBikesResponses.add(fetchBikesResponse);
        }
        response.put("bikes", fetchBikesResponses);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchBikesByClient(BikeSessionRequest request){
        Map param = request.getParam();
        FetchBikeRequest fetchBikeRequest = map(param, FetchBikeRequest.class);
        List<Leases> leases = leaseRepository.findAllByClients_ClientIdOrderByLeaseInfo_ContractDate(fetchBikeRequest.getClientId());
        List<Bikes> bikes = new ArrayList<>();
        for(Leases lease : leases)
            bikes.add(lease.getBike());
        List<FetchBikesResponse> fetchBikesResponses = new ArrayList<>();
        Map response = new HashMap();
        for(Bikes bike : bikes){
            FetchBikesResponse fetchBikesResponse = new FetchBikesResponse();
            CommonCodeBikes carModel = bike.getCarModel();
            fetchBikesResponse.setColor(bike.getColor());
            fetchBikesResponse.setNumber(bike.getCarNum());
            CarModel model = new CarModel();
            model.setCarModelCode(carModel.getCode());
            model.setCarModelName(carModel.getModel());
            fetchBikesResponse.setModel(model);
            fetchBikesResponse.setYears(bike.getYears());
            fetchBikesResponse.setVimNum(bike.getVimNum());
            fetchBikesResponse.setBikeId(bike.getBikeId());
            fetchBikesResponses.add(fetchBikesResponse);
        }
        response.put("bikes", fetchBikesResponses);
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchBikeDetail(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        FetchBikeRequest fetchBikeRequest = map(param, FetchBikeRequest.class);
        fetchBikeRequest.checkValidation();
        Bikes bike = bikesRepository.findByBikeId(fetchBikeRequest.getBikeId());
        Leases leases = leaseRepository.findByBikeNo(bike.getBikeNo());
        Clients clients = leases == null ? null : leases.getClients();
        FetchBikeDetailResponse fetchBikeDetailResponse = new FetchBikeDetailResponse();
        CommonCodeBikes carModel = bike.getCarModel();
        CarModel model = new CarModel();
        model.setCarModelCode(carModel.getCode());
        model.setCarModelName(carModel.getModel());
        model.setBikeType(carModel.getBikeType());
        model.setVolume(carModel.getVolume());
        fetchBikeDetailResponse.setYears(bike.getYears());
        fetchBikeDetailResponse.setVolume(bike.getVolume());
        fetchBikeDetailResponse.setModel(model);
        fetchBikeDetailResponse.setBikeId(bike.getBikeId());
        fetchBikeDetailResponse.setColor(bike.getColor());
        fetchBikeDetailResponse.setVimNum(bike.getVimNum() == null ? "" : bike.getVimNum());
        fetchBikeDetailResponse.setCarNum(bike.getCarNum());
        fetchBikeDetailResponse.setReceiveDt(bike.getReceiveDate());
        fetchBikeDetailResponse.setRegisterDt(bike.getRegisterDate());
        fetchBikeDetailResponse.setTransaction(bike.getTransaction());
        if(leases != null) {
            ClientDto client = new ClientDto();
            client.setClientName(clients.getClientInfo().getName());
            client.setClientId(clients.getClientId());
            LeasesDto lease = new LeasesDto();
            lease.setLeaseId(leases.getLeaseId());

            fetchBikeDetailResponse.setClient(client);
            fetchBikeDetailResponse.setLease(lease);
        }
        response.put("bike", fetchBikeDetailResponse);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addBike(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser session = request.getSessionUser();
        AddBikeRequest addBikeRequest = map(param, AddBikeRequest.class);
        addBikeRequest.checkValidation();
        if(bePresent(bikesRepository.findByVimNum(addBikeRequest.getVimNumber()))) withException("500-009");
        if(bePresent(addBikeRequest.getNumber()) && bePresent(bikesRepository.findByCarNum(addBikeRequest.getNumber()))) withException("500-011");

        String bikeId = autoKey.makeGetKey("bike");
        Bikes bike = new Bikes();
        bike.setBikeId(bikeId);
        bike.setYears(addBikeRequest.getYears());
        bike.setVimNum(addBikeRequest.getVimNumber());
        bike.setCarNum(addBikeRequest.getNumber());
        bike.setCarModelCode(addBikeRequest.getCarModel());
        bike.setColor(addBikeRequest.getColor());
        bike.setReceiveDate(addBikeRequest.getReceiveDt());
        ModelTransaction modelTransaction = new ModelTransaction();
        modelTransaction.setRegNum(addBikeRequest.getRegNum());
        modelTransaction.setPrice(addBikeRequest.getPrice());
        modelTransaction.setCompanyName(addBikeRequest.getCompanyName());
        bike.setTransaction(modelTransaction);
        bikesRepository.save(bike);
        CommonCodeBikes model = bikeModelsRepository.findByCode(addBikeRequest.getCarModel());
        String log = "<>" + addBikeRequest.getYears() + "</>년식 차량모델 배기량은 <>"
                + (model.getBikeType().equals(BikeTypes.GAS)? model.getVolume() + " cc" : model.getVolume() + " KW") +
                "</> 색상은 <>" + addBikeRequest.getColor() + "</> 차대번호가 <>" + addBikeRequest.getVimNumber() + "</> 인 바이크가 생성되었습니다";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_ADDED, session.getUserNo(), bike.getBikeNo().toString(), log));

        return request;
    }

    @Transactional
    public BikeSessionRequest updateBike(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateBikeRequest updateBikeRequest = map(param, UpdateBikeRequest.class);
        Bikes bike = bikesRepository.findByBikeId(updateBikeRequest.getBikeId());
        updateBikeRequest.checkValidation();
        ModelTransaction modelTransaction = new ModelTransaction();
        modelTransaction.setRegNum(updateBikeRequest.getRegNum());
        modelTransaction.setPrice(updateBikeRequest.getPrice());
        modelTransaction.setCompanyName(updateBikeRequest.getCompanyName());
        Leases leases = leaseRepository.findByBikeNo(bike.getBikeNo());
        if(leases != null) {
            List<LeaseExpense> expenses = expenseRepository.findAllByLease_LeaseIdAndExpenseTypes(leases.getLeaseId(), ExpenseTypes.BIKE);
            LeaseExpense leaseExpense;
            if (expenses.size() > 0) {
                leaseExpense = expenses.get(0);
                leaseExpense.setTransaction(modelTransaction);
                expenseRepository.save(leaseExpense);
            } else {
                leaseExpense = new LeaseExpense();
                leaseExpense.setLeaseNo(leases.getLeaseNo());
                leaseExpense.setExpenseTypes(ExpenseTypes.BIKE);
                leaseExpense.setTransaction(modelTransaction);
                leaseExpense.setNumber(1);
                expenseRepository.save(leaseExpense);
            }
            List<LeaseExpense> expenseList = expenseRepository.findAllByLease_LeaseIdAndExpenseTypes(leases.getLeaseId(), ExpenseTypes.REGISTER);
            if (expenseList.size() > 0) {
                leaseExpense = expenseList.get(0);
                leaseExpense.setTransaction(modelTransaction);
                expenseRepository.save(leaseExpense);
            } else {
                LeaseExpense expenseReg = new LeaseExpense();
                expenseReg.setLeaseNo(leases.getLeaseNo());
                expenseReg.setExpenseTypes(ExpenseTypes.REGISTER);
                ModelTransaction transaction = new ModelTransaction();
                //취등록세 공식 2%
                transaction.setPrice(bike.getTransaction() == null || bike.getTransaction().getPrice() == null ? null : bike.getTransaction().getPrice() / 50);
                transaction.setRegNum("-");
                transaction.setCompanyName("-");
                expenseReg.setTransaction(transaction);
                expenseReg.setNumber(1);
                expenseRepository.save(expenseReg);
            }
        }
        if(!updateBikeRequest.getVimNumber().equals(bike.getVimNum()) && bikesRepository.countAllByVimNum(updateBikeRequest.getVimNumber()) > 0) withException("500-009");
        if(bePresent(updateBikeRequest.getNumber()) && (!updateBikeRequest.getNumber().equals(bike.getCarNum()) && bikesRepository.countAllByCarNum(updateBikeRequest.getNumber()) > 0)) withException("500-011");
        updateBikeInfoWithLog(updateBikeRequest, request.getSessionUser(), bike);
        bike.setYears(updateBikeRequest.getYears());
        bike.setVimNum(updateBikeRequest.getVimNumber());
        bike.setCarNum(updateBikeRequest.getNumber());
        bike.setCarModelCode(updateBikeRequest.getCarModel());
        bike.setColor(updateBikeRequest.getColor());
        bike.setReceiveDate(updateBikeRequest.getReceiveDt());
        bike.setRegisterDate(updateBikeRequest.getRegisterDt());
        bike.setTransaction(modelTransaction);
        bikesRepository.save(bike);

        return request;
    }

    private void updateBikeInfoWithLog(UpdateBikeRequest updateBikeRequest, BikeUser session, Bikes bike){
        List<String> stringList = new ArrayList<>();
        if(bePresent(updateBikeRequest)){

            if(bePresent(updateBikeRequest.getVimNumber()) && !updateBikeRequest.getVimNumber().equals(bike.getVimNum())){
                stringList.add("바이크 차대 번호를 <>" + bike.getVimNum() + "</>에서 <>" + updateBikeRequest.getVimNumber() + "</>으로 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getNumber()) && !updateBikeRequest.getNumber().equals(bike.getCarNum())){
                String log = bike.getCarNum() == null ? "바이크 차량번호가 <>" + updateBikeRequest.getNumber() + "</>로/으로 설정했습니다." : "바이크 차량번호를 <>" + bike.getCarNum() + "</>에서 <>" + updateBikeRequest.getNumber() + "</>으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(updateBikeRequest.getCarModel()) && !updateBikeRequest.getCarModel().equals(bike.getCarModelCode())){
                CommonCodeBikes change = bikeModelsRepository.findByCode(updateBikeRequest.getCarModel());
                String exModel = bike.getCarModel().getBikeType().equals(BikeTypes.GAS)? bike.getCarModel().getModel() + " / " + bike.getCarModel().getVolume() + " cc" : bike.getCarModel().getModel() + " / " + bike.getCarModel().getVolume() + " KW";
                String nowModel = change.getBikeType().equals(BikeTypes.GAS)? change.getModel() + " / " + change.getVolume() + " cc" : change.getModel() + " / " + change.getVolume() + " KW";
                stringList.add("바이크 차량종류를 <>" + exModel + "</>에서 <>" + nowModel +"</>로 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getColor()) && !updateBikeRequest.getColor().equals(bike.getColor())){
                String log = bike.getColor() == null ? "바이크 색상을 <>" + updateBikeRequest.getColor() + "</>로/으로 설정했습니다." : "바이크 색상을 <>" + bike.getColor() + "</>에서 <>" + updateBikeRequest.getColor() + "</>으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(updateBikeRequest.getReceiveDt()) && !updateBikeRequest.getReceiveDt().equals(bike.getReceiveDate())){
                String log = bike.getReceiveDate() == null ? "바이크 수령일자를 <>" + updateBikeRequest.getReceiveDt() + "</>로/으로 설정했습니다." : "바이크 수령일자를 <>" + bike.getReceiveDate() + "</>에서 <>" + updateBikeRequest.getReceiveDt() + "</>으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(updateBikeRequest.getRegisterDt()) && !updateBikeRequest.getRegisterDt().equals(bike.getRegisterDate())){
                String log = bike.getRegisterDate() == null ? "바이크 등록일을 <>" + updateBikeRequest.getRegisterDt() + "</>로/으로 설정했습니다." : "바이크 등록일을 <>" + bike.getRegisterDate() + "</>에서 <>" + updateBikeRequest.getRegisterDt() + "</>으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(updateBikeRequest.getYears()) && !updateBikeRequest.getYears().equals(bike.getYears())){
                String log = bike.getYears() == null ? "바이크 연식을 <>" + updateBikeRequest.getYears() + "</>로/으로 설정했습니다." : "바이크 연식을 <>" + bike.getYears() + "</>에서 <>" + updateBikeRequest.getYears() + "</>으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(updateBikeRequest.getCompanyName()) && (bike.getTransaction() == null || !updateBikeRequest.getCompanyName().equals(bike.getTransaction().getCompanyName()))){
                String log = bike.getTransaction() == null || bike.getTransaction().getCompanyName() == null ? "바이크의 구입처를 <>" + updateBikeRequest.getCompanyName() + "</>로/으로 설정했습니다." : "바이크의 구입처를 <>" + bike.getTransaction().getCompanyName() + "</>에서 <>" + updateBikeRequest.getCompanyName() + "</>로/으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(updateBikeRequest.getRegNum()) && (bike.getTransaction() == null || !updateBikeRequest.getRegNum().equals(bike.getTransaction().getRegNum()))){
                String log = bike.getTransaction() == null || bike.getTransaction().getRegNum() == null ? "바이크 구입처 사업자 번호를 \"<>" + updateBikeRequest.getRegNum() + "</>\"로/으로 설정했습니다." : "바이크 구입처 사업자 번호를 \"<>" + bike.getTransaction().getRegNum() + "</>\"에서 \"<>" + updateBikeRequest.getRegNum() + "</>\"로/으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(updateBikeRequest.getPrice()) && (bike.getTransaction() == null || !updateBikeRequest.getPrice().equals(bike.getTransaction().getPrice()))){
                String log = bike.getTransaction() == null || bike.getTransaction().getPrice() == null ? "바이크의 구매가격을 <>" + Utils.getCurrencyFormat(updateBikeRequest.getPrice()) + "원</>로/으로 설정했습니다." : "바이크의 구매가격을 <>" + Utils.getCurrencyFormat(bike.getTransaction().getPrice()) + "원</>에서 <>" + Utils.getCurrencyFormat(updateBikeRequest.getPrice()) + "원</>로/으로 변경하였습니다.";
                stringList.add(log);
            }
            if(bePresent(stringList) && stringList.size() > 0){
                bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_UPDATED, session.getUserNo(), bike.getBikeNo().toString(), stringList));
            }
        }

    }

    @Transactional
    public BikeSessionRequest deleteBike(BikeSessionRequest request){
        Map param = request.getParam();
        DeleteBikeRequest deleteBikeRequest  = map(param, DeleteBikeRequest.class);
        deleteBikeRequest.checkValidation();
        Bikes bikes = bikesRepository.findByBikeId(deleteBikeRequest.getBikeId());
        List<Leases> leases = leaseRepository.findAllByBike_BikeId(deleteBikeRequest.getBikeId());
        if(bikes == null) withException("");
        if(leases.size() > 0) writeMessage("리스번호 " + leases.get(0).getLeaseId() + "가 이미 연결되어 있습니다.");
        else{
            bikesRepository.delete(bikes);
        }
        bikeUserLogRepository.deleteAllByLogTypeAndReferenceId(BikeUserLogTypes.COMM_BIKE_ADDED, bikes.getBikeNo().toString());
        bikeUserLogRepository.deleteAllByLogTypeAndReferenceId(BikeUserLogTypes.COMM_BIKE_UPDATED, bikes.getBikeNo().toString());
        List<BikeAttachments> allByBike_bikeId = bikeAttachmentRepository.findAllByBike_BikeId(bikes.getBikeId());
        bikeAttachmentRepository.deleteAll(allByBike_bikeId);
        return request;
    }

    public BikeSessionRequest fetchBikeVolumes(BikeSessionRequest request){
        List<CommonCodeBikes> commonCodeBikes = bikeModelsRepository.findAll();
        List<String> volumeList = new ArrayList<>();
        for(CommonCodeBikes ccb : commonCodeBikes){
            if(ccb.getBikeType().equals(BikeTypes.GAS))
                volumeList.add(ccb.getVolume() + " cc");
            else
                volumeList.add(ccb.getVolume() + " KW");
        }
        volumeList = List.copyOf(Set.copyOf(volumeList));
        request.setResponse(volumeList);
        return request;
    }

    public BikeSessionRequest fetchBikeModels(BikeSessionRequest request){
        List<CommonCodeBikes> commonCodeBikes = bikeModelsRepository.findAll();
        request.setResponse(commonCodeBikes);
        return request;
    }

    public BikeSessionRequest fetchBikeModelsByVolume(BikeSessionRequest request){
        Double volume = Double.parseDouble((String) request.getParam().get("volume"));
        Map response = new HashMap();
        List<CommonCodeBikes> commonCodeBikes = bikeModelsRepository.findAllByVolume(volume);
        List<FetchBikeModelsResponse> fetchBikeModelsResponses = new ArrayList<>();
        for(CommonCodeBikes model: commonCodeBikes){
            if(model.getDiscontinue())
                continue;
            FetchBikeModelsResponse fetchBikeModelsResponse = new FetchBikeModelsResponse();
            fetchBikeModelsResponse.setModel(model.getModel());
            fetchBikeModelsResponse.setCode(model.getCode());
            fetchBikeModelsResponse.setDiscontinue(model.getDiscontinue());
            fetchBikeModelsResponse.setBikeType(model.getBikeType());
            fetchBikeModelsResponse.setVolume(model.getVolume());
            fetchBikeModelsResponses.add(fetchBikeModelsResponse);
        }
        response.put("model", fetchBikeModelsResponses);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addBikeModel(BikeSessionRequest request){
        Map param = request.getParam();
        BikeModelDto bikeModelDto = map(param, BikeModelDto.class);
        bikeModelDto.checkValidation();
        Manufacturers manufacturerById = bikeWorker.getManufacturerById(bikeModelDto.getManufacturerNo());
        bikeModelDto.setManufacturers(manufacturerById);
        List<CommonCodeBikes> models = bikeModelsRepository.findAll();
        int lastNum = Integer.parseInt(models.get(models.size() - 1).getCode().split("-")[1]);
        CommonCodeBikes codeBike = new CommonCodeBikes();
        codeBike.setCode("001-" + String.format("%03d", lastNum + 1));
        codeBike.updateData(bikeModelDto);
        bikeModelsRepository.save(codeBike);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateBikeModel(BikeSessionRequest request){
        Map param = request.getParam();
        BikeModelDto bikeModelDto = map(param, BikeModelDto.class);
        bikeModelDto.checkValidation();
        Manufacturers manufacturerById = bikeWorker.getManufacturerById(bikeModelDto.getManufacturerNo());
        bikeModelDto.setManufacturers(manufacturerById);
        CommonCodeBikes codeBike = bikeModelsRepository.findByCode(bikeModelDto.getCode());
        codeBike.updateData(bikeModelDto);
        bikeModelsRepository.save(codeBike);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteBikeModel(BikeSessionRequest request){
        Map param = request.getParam();

        return request;
    }

    public BikeSessionRequest generatePreSignedURLToUploadBikeFile(BikeSessionRequest request){
        Map param = request.getParam();
        BikeDto bikeDto = map(param, BikeDto.class);
        Bikes bike = bikesRepository.findByBikeId(bikeDto.getBikeId());
        if(!bePresent(bikeDto.getFilename())) withException("");
        String uuid = UUID.randomUUID().toString();
        String filename = bikeDto.getFilename().substring(0, bikeDto.getFilename().lastIndexOf("."));
        String extension =  bikeDto.getFilename().substring(bikeDto.getFilename().lastIndexOf(".")+1);
        PresignedURLVo presignedURLVo = new PresignedURLVo();
        presignedURLVo.setBucket(ENV.AWS_S3_QUEUE_BUCKET);
        presignedURLVo.setFileKey("bikes/" + bike.getBikeId() + "/" + uuid + "." + filename + "." + extension);
        presignedURLVo.setFilename(filename + "." + extension);
        presignedURLVo.setUrl(AmazonUtils.AWSGeneratePresignedURL(presignedURLVo));
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public BikeSessionRequest checkFileUploadComplete(BikeSessionRequest request){
        Map param = request.getParam();
        PresignedURLVo presignedURLVo = map(param, PresignedURLVo.class);
        String bikeId = (String) param.get("bike_id");
        Bikes bike = bikesRepository.findByBikeId(bikeId);
        BikeAttachments bikeAttachments = new BikeAttachments();
        bikeAttachments.setBikeNo(bike.getBikeNo());
        bikeAttachments.setFileName(presignedURLVo.getFilename());
        bikeAttachments.setFileKey("/" + presignedURLVo.getFileKey());
        bikeAttachments.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
        bikeAttachmentRepository.save(bikeAttachments);
        //
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(AmazonUtils.awsCredentialsProvider())
                .build();
        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, presignedURLVo.getFileKey());
        amazonS3.copyObject(objectRequest);
        String log = "바이크에 <>" + presignedURLVo.getFilename() +"</> 파일명의 파일이 추가 되었습니다.";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_UPDATED, request.getSessionUser().getUserNo(), bike.getBikeNo().toString(), log));
        Map response = new HashMap();
        response.put("url", bikeAttachments.getFileKey());
        request.setResponse(response);
        return request;
    }


    public BikeSessionRequest fetchFilesByBike(BikeSessionRequest request){
        Map param = request.getParam();
        List<BikeAttachments> attachments = bikeAttachmentRepository.findAllByBike_BikeId((String) param.get("bike_id"));
        request.setResponse(attachments);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteFile(BikeSessionRequest request){
        Map param = request.getParam();
        Integer attachmentNo = Integer.parseInt((String)param.get("bike_attachment_no"));
        BikeAttachments byBikeFileInfoNo = bikeAttachmentRepository.findByBikeFileInfoNo(attachmentNo);
        String log = "바이크에 <>" + byBikeFileInfoNo.getFileName() +"</> 파일명의 파일이 삭제 되었습니다.";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_UPDATED, request.getSessionUser().getUserNo(), byBikeFileInfoNo.getBike().getBikeNo().toString(), log));
        String url = byBikeFileInfoNo.getDomain() + byBikeFileInfoNo.getFileKey();
        bikeAttachmentRepository.deleteById(byBikeFileInfoNo.getBikeFileInfoNo());
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(AmazonUtils.awsCredentialsProvider())
                .build();
        amazonS3.deleteObject(ENV.AWS_S3_ORIGIN_BUCKET, url);
        return request;
    }


}
