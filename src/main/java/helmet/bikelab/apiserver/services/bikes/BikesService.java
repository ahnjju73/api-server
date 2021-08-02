package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.bike.BikeAttachments;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserInfo;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
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
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class BikesService extends SessService {

    private final BikeAttachmentRepository bikeAttachmentRepository;
    private final BikesRepository bikesRepository;
    private final LeaseRepository leaseRepository;
    private final AutoKey autoKey;
    private final BikeModelsRepository bikeModelsRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final ClientsRepository clientsRepository;
    private final CommonWorker commonWorker;


    public BikeSessionRequest fetchHistoriesByBikeId(BikeSessionRequest request){
        BikeListDto bikeListDto = map(request.getParam(), BikeListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(bikeListDto, "bikelabs.clients_log.fetchClientHistoriesByClientId", "bikelabs.clients_log.countAllClientHistoriesByClientId", "log_no");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchGroupDetailsByGroupId(BikeSessionRequest request){
        Map param = request.getParam();
        String groupId = (String)param.get("group_id");
        Map details = (Map)getItem("bikelabs.commons.clients.fetchGroupDetailsByGroupId", param);
        request.setResponse(details);
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
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.commons.bikes.fetchBikesList", "bikelabs.commons.bikes.countAllBikeList", "bike_id");
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

//    public BikeSessionRequest fetchBikeByBikeNum(BikeSessionRequest request){
//        Map param = request.getParam();
//        FetchBikeRequest fetchBikeRequest = map(param, FetchBikeRequest.class);
//
//    }
//
//    public BikeSessionRequest fetchBikesByClientName(BikeSessionRequest request){
//
//    }



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
        fetchBikeDetailResponse.setYears(bike.getYears());
        fetchBikeDetailResponse.setVolume(bike.getVolume());
        fetchBikeDetailResponse.setModel(model);
        fetchBikeDetailResponse.setBikeId(bike.getBikeId());
        fetchBikeDetailResponse.setColor(bike.getColor());
        fetchBikeDetailResponse.setVimNum(bike.getVimNum() == null ? "" : bike.getVimNum());
        fetchBikeDetailResponse.setCarNum(bike.getCarNum());
        fetchBikeDetailResponse.setReceiveDt(bike.getReceiveDate());
        fetchBikeDetailResponse.setRegisterDt(bike.getRegisterDate());
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
        if(bePresent(bikesRepository.findByVimNum(addBikeRequest.getVimNumber()))) withException("500-007");
        String bikeId = autoKey.makeGetKey("bike");
        Bikes bike = new Bikes();
        bike.setBikeId(bikeId);
        bike.setYears(addBikeRequest.getYears());
        bike.setVimNum(addBikeRequest.getVimNumber());
        bike.setCarNum(addBikeRequest.getNumber());
        bike.setCarModelCode(addBikeRequest.getCarModel());
        bike.setColor(addBikeRequest.getColor());
        bike.setReceiveDate(addBikeRequest.getReceiveDt());
        bikesRepository.save(bike);

        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_ADDED, session.getUserNo(), bike.getBikeNo().toString()));

        return request;
    }

    @Transactional
    public BikeSessionRequest updateBike(BikeSessionRequest request){
        Map param = request.getParam();
        UpdateBikeRequest updateBikeRequest = map(param, UpdateBikeRequest.class);
        Bikes bike = bikesRepository.findByBikeId(updateBikeRequest.getBikeId());
        if(updateBikeRequest.getVimNumber().equals(bike.getVimNum())&&!bike.equals(bikesRepository.findByVimNum(updateBikeRequest.getVimNumber()))) withException("");
        if(updateBikeRequest.getNumber().equals(bike.getCarNum())&&!bike.equals(bikesRepository.findByCarNum(updateBikeRequest.getNumber()))) withException("");
        updateBikeInfoWithLog(updateBikeRequest, request.getSessionUser(), bike);
        bike.setYears(updateBikeRequest.getYears());
        bike.setVimNum(updateBikeRequest.getVimNumber());
        bike.setCarNum(updateBikeRequest.getNumber());
        bike.setCarModelCode(updateBikeRequest.getCarModel());
        bike.setColor(updateBikeRequest.getColor());
        bike.setReceiveDate(updateBikeRequest.getReceiveDt());
        bike.setRegisterDate(updateBikeRequest.getRegisterDt());
        bikesRepository.save(bike);

        return request;
    }

    private void updateBikeInfoWithLog(UpdateBikeRequest updateBikeRequest, BikeUser session, Bikes bike){
        List<String> stringList = new ArrayList<>();
        if(bePresent(updateBikeRequest)){
            if(bePresent(updateBikeRequest.getYears()) && !updateBikeRequest.getYears().equals(bike.getYears())){
                stringList.add("바이크 연식정보를 <>" + bike.getYears() + "</>에서 <>" + updateBikeRequest.getYears() + "</>으로 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getVimNumber()) && !updateBikeRequest.getVimNumber().equals(bike.getVimNum())){
                stringList.add("바이크 vim Number를 <>" + bike.getVimNum() + "</>에서 <>" + updateBikeRequest.getVimNumber() + "</>으로 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getNumber()) && !updateBikeRequest.getNumber().equals(bike.getCarNum())){
                stringList.add("바이크 차량번호를 <>" + bike.getCarNum() + "</>에서 <>" + updateBikeRequest.getNumber() + "</>으로 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getCarModel()) && !updateBikeRequest.getCarModel().equals(bike.getCarModelCode())){
                stringList.add("바이크 차량종류를 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getColor()) && !updateBikeRequest.getColor().equals(bike.getColor())){
                stringList.add("바이크 차량 색상을 <>" + bike.getColor() + "</>에서 <>" + updateBikeRequest.getColor() + "</>으로 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getReceiveDt()) && !updateBikeRequest.getReceiveDt().equals(bike.getReceiveDate())){
                stringList.add("바이크 수령일을 <>" + bike.getReceiveDate().toLocalDate() + "</>에서 <>" + updateBikeRequest.getReceiveDt().toLocalDate() + "</>으로 변경하였습니다.");
            }
            if(bePresent(updateBikeRequest.getRegisterDt()) && !updateBikeRequest.getRegisterDt().equals(bike.getRegisterDate())){
                stringList.add("바이크 등록일을 <>" + bike.getRegisterDate().toLocalDate() + "</>에서 <>" + updateBikeRequest.getRegisterDt().toLocalDate() + "</>으로 변경하였습니다.");
            }
            if(bePresent(stringList) && stringList.size() > 0){
                bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_BIKE_ADDED, session.getUserNo(), bike.getBikeNo().toString(), stringList));
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
        if(leases.size() == 0) writeMessage("리스번호 " + leases.get(0).getLeaseId() + "가 이미 연결되어 있습니다.");
        else{
            bikesRepository.delete(bikes);
        }
        return request;
    }

    public BikeSessionRequest fetchBikeModels(BikeSessionRequest request){
        Map response = new HashMap();
        List<CommonCodeBikes> commonCodeBikes = bikeModelsRepository.findAll();
        List<FetchBikeModelsResponse> fetchBikeModelsResponses = new ArrayList<>();
        for(CommonCodeBikes model: commonCodeBikes){
              FetchBikeModelsResponse fetchBikeModelsResponse = new FetchBikeModelsResponse();
              fetchBikeModelsResponse.setModel(model.getModel());
              fetchBikeModelsResponse.setCode(model.getCode());
              fetchBikeModelsResponse.setDiscontinue(model.getDiscontinue());
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
        CommonCodeBikes codeBike = new CommonCodeBikes();
        String modelCode = autoKey.makeGetKey("model");
        codeBike.setCode(modelCode);
        codeBike.setModel(bikeModelDto.getModel());
        codeBike.setDiscontinue(bikeModelDto.getDiscontinue());
        bikeModelsRepository.save(codeBike);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateBikeModel(BikeSessionRequest request){
        Map param = request.getParam();
        BikeModelDto bikeModelDto = map(param, BikeModelDto.class);
        CommonCodeBikes codeBike = bikeModelsRepository.findByCode(bikeModelDto.getCode());
        codeBike.setModel(bikeModelDto.getModel());
        codeBike.setDiscontinue(bikeModelDto.getDiscontinue());
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
        presignedURLVo.setFileKey("bikes/" + bike.getBikeId() + "/" + uuid + "." + filename + extension);
        presignedURLVo.setFilename(filename+extension);
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
        bikeAttachments.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
        bikeAttachments.setUrl("/" + presignedURLVo.getFileKey());
        // todo: filename required
        bikeAttachmentRepository.save(bikeAttachments);
        //
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(AmazonUtils.awsCredentialsProvider())
                .build();
        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, presignedURLVo.getFileKey());
        amazonS3.copyObject(objectRequest);
        Map response = new HashMap();
        response.put("url", bikeAttachments.getUrl());
        request.setResponse(response);
        return request;
    }

}
