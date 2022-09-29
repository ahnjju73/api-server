package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.CommonWorking;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.objects.requests.BikePartsRequest;
import helmet.bikelab.apiserver.objects.requests.ModelPartsExcelRequest;
import helmet.bikelab.apiserver.objects.requests.PartsCodeListRequest;
import helmet.bikelab.apiserver.objects.requests.PartsExcelRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.CommonWorkingRepository;
import helmet.bikelab.apiserver.repositories.PartsCodesRepository;
import helmet.bikelab.apiserver.repositories.PartsRepository;
import helmet.bikelab.apiserver.repositories.PartsTypesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BikePartsService extends SessService {

    private final CommonWorker commonWorker;
    private final PartsRepository partsRepository;
    private final BikeWorker bikeWorker;
    private final PartsTypesRepository partsTypesRepository;
    private final CommonWorkingRepository commonWorkingRepository;
    private final PartsCodesRepository partsCodesRepository;

    public BikeSessionRequest fetchCommonWorkingPriceList(BikeSessionRequest request){
        Map param = request.getParam();
        BikeTypes bikeType = BikeTypes.getType((String)param.get("bike_type"));
        List<CommonWorking> all = commonWorkingRepository.findByBikeType(bikeType);
        request.setResponse(all);
        return request;
    }

    public BikeSessionRequest fetchPartImageByPartsId(BikeSessionRequest request){
        PartsByIdRequest partsByIdRequest = map(request.getParam(), PartsByIdRequest.class);
        Parts partsById = bikeWorker.getPartsById(partsByIdRequest.getPartsNo());
        List<ImageVo> images = partsById.getImages();
        request.setResponse(!bePresent(images) ? new ArrayList() : images);
        return request;
    }

    @Transactional
    public BikeSessionRequest deletePartsImage(BikeSessionRequest request){
        DeletePartsImageRequest partsByIdRequest = map(request.getParam(), DeletePartsImageRequest.class);
        Parts partsById = bikeWorker.getPartsById(partsByIdRequest.getPartsNo());
        List<ImageVo> images = partsById.getImages();
        if(bePresent(images)){
            for(int i = 0; i < images.size(); i++){
                ImageVo partsImages = images.get(i);
                if(partsImages.getId().equals(partsByIdRequest.getId())){
                    images.remove(i);
                    break;
                }
            }
            partsById.setImages(images);
            partsRepository.save(partsById);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest addNewPartsImage(BikeSessionRequest request){
        Map param = request.getParam();
        AddPartsImageRequest addPartsImageRequest = map(param, AddPartsImageRequest.class);
        Parts partsByIdAndCarModel = bikeWorker.getPartsById(addPartsImageRequest.getPartsNo());

        if(bePresent(addPartsImageRequest.getImages())){
            List<ImageVo> images = partsByIdAndCarModel.getImages();
            List<ImageVo> collect = addPartsImageRequest
                    .getImages()
                    .stream().map(presignedURLVo -> {
                        AmazonS3 amazonS3 = AmazonUtils.amazonS3();
                        String fileKey = "parts/" + partsByIdAndCarModel.getPartNo() + "/" + presignedURLVo.getFileKey();
                        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                        amazonS3.copyObject(objectRequest);
                        ImageVo partsImage = new ImageVo(MediaTypes.IMAGE, presignedURLVo.getFilename(), fileKey);
                        return partsImage;
                    }).collect(Collectors.toList());
            if(!bePresent(images)) images = new ArrayList<>();
            images.addAll(collect);
            partsByIdAndCarModel.setImages(images);
            partsRepository.save(partsByIdAndCarModel);
        }
        return request;
    }

    public BikeSessionRequest generatePresignedUrl(BikeSessionRequest request){
        Map param = request.getParam();
        String filename = (String)param.get("filename");
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(filename, null);
        request.setResponse(presignedURLVo);
        return request;
    }

    public BikeSessionRequest fetchPartsByID(BikeSessionRequest request){
        PartsByIdRequest partsByIdRequest = map(request.getParam(), PartsByIdRequest.class);
        Parts partsById = bikeWorker.getPartsById(partsByIdRequest.getPartsNo());
        request.setResponse(partsById);
        return request;
    }

    public BikeSessionRequest fetchParts(BikeSessionRequest request) {
        Map param = request.getParam();
        BikePartsRequest bikePartsDto = map(param, BikePartsRequest.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(bikePartsDto, "comm.parts.fetchAllParts", "comm.parts.countAllParts", "parts_no");
        request.setResponse(responseListDto);
        return request;
    }

    @Transactional
    public BikeSessionRequest deletePartsByIdAndCarModel(BikeSessionRequest request){
        return request;
    }

    @Transactional
    public BikeSessionRequest updatePartsByIdAndCarModel(BikeSessionRequest request){
        PartsUpdatedRequest partsUpdatedRequest = map(request.getParam(), PartsUpdatedRequest.class);
        partsUpdatedRequest.checkValidation();
        Parts partsByIdAndCarModel = bikeWorker.getPartsById(partsUpdatedRequest.getPartsNo());
        Boolean changed = false;
        PartsBackUpDto partsBackUpDto = map(partsByIdAndCarModel, PartsBackUpDto.class);

//        if(!partsUpdatedRequest.getIsFreeSupport().equals(partsByIdAndCarModel.getIsFreeSupport())){
//            partsByIdAndCarModel.setIsFreeSupport(partsUpdatedRequest.getIsFreeSupport());
//            changed = true;
//        }
        if(!partsUpdatedRequest.getPartsId().equals(partsByIdAndCarModel.getPartsId())){
            partsByIdAndCarModel.setPartsId(partsUpdatedRequest.getPartsId());
            changed = true;
        }
        if(!partsUpdatedRequest.getPartsPrices().equals(partsByIdAndCarModel.getPartsPrices())){
            partsByIdAndCarModel.setPartsPrices(partsUpdatedRequest.getPartsPrices());
            changed = true;
        }
        if(!partsUpdatedRequest.getWorkingHours().equals(partsByIdAndCarModel.getWorkingHours())){
            partsByIdAndCarModel.setWorkingHours(partsUpdatedRequest.getWorkingHours());
            changed = true;
        }

        if(changed){
            List<PartsBackUpDto> backUpList = partsByIdAndCarModel.getBackUpList();
            if(!bePresent(backUpList)) backUpList = new ArrayList<>();
            backUpList.add(partsBackUpDto);
            partsByIdAndCarModel.setBackUpList(backUpList);
            partsRepository.save(partsByIdAndCarModel);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest addPartsByModel(BikeSessionRequest request) {
        Map param = request.getParam();
        PartsNewRequest bikePartsDto = map(param, PartsNewRequest.class);
        bikePartsDto.checkValidation();
        Parts parts = new Parts();
        CommonBikes model = bikeWorker.getCommonCodeBikesById(bikePartsDto.getCarModel());
        PartsCodes partsCodes = bikeWorker.getPartsCodeById(bikePartsDto.getPartsCodeNo());
        parts.setPartsId(bikePartsDto.getPartsId());
        parts.setBikeModelCode(model.getCode());
        parts.setPartsCodeNo(partsCodes.getPartsCodeNo());
        parts.setPartsPrices(bikePartsDto.getPartsPrices());
        parts.setWorkingHours(bikePartsDto.getWorkingHours());
        parts.setUnits(bikePartsDto.getUnits());
        partsRepository.save(parts);
        return request;
    }

    public BikeSessionRequest fetchPartsCodes(BikeSessionRequest request){
        List response = getList("comm.parts.fetchPartsCodes", request.getParam());
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchPartsTypes(BikeSessionRequest request){
        List<PartsTypes> allBy = partsTypesRepository.findAllBy();
        request.setResponse(allBy);
        return request;
    }

    @Transactional
    public BikeSessionRequest uploadNewParts(BikeSessionRequest request) {
        Map param = request.getParam();
        PartsExcelRequest partsExcelRequest = map(param, PartsExcelRequest.class);
        List<PartsCodeListRequest> parts = partsExcelRequest.getParts();
        String errors = "";
        for (int i = 0; i < parts.size(); i++){
            String partsName = parts.get(i).getPartsName();
            String partsType = parts.get(i).getPartsType();
            PartsTypes partsTypes = partsTypesRepository.findByPartsType(partsType);
            if(!bePresent(partsTypes)){
                errors += i + " 번째 파트타입이 현재 존재하지 않는 파트타입 입니다.\n";
                continue;
            }
            PartsCodes partsCodes = new PartsCodes();
            partsCodes.setPartsTypeNo(partsTypes.getPartsTypeNo());
            partsCodes.setPartsName(partsName);
            partsCodesRepository.save(partsCodes);
        }
        if(!errors.equals("")){
            writeMessage(errors);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest uploadModelParts(BikeSessionRequest request) {
        Map param = request.getParam();
        ModelPartsExcelRequest partsExcelRequest = map(param, ModelPartsExcelRequest.class);
        List<BikePartsRequest> parts = partsExcelRequest.getParts();
        String errors = "";
        for (int i = 0; i < parts.size(); i++) {
            String init = i + 1 + "번째\n";
            String error = init;
            String carModel = parts.get(i).getCarModel();
            String partsId = parts.get(i).getPartsId();
            String partsName = parts.get(i).getPartsName();
            Integer partsPrice = parts.get(i).getPartsPrice();
            Double workingHour = parts.get(i).getWorkingHour();
            if(bePresent(partsRepository.findByPartsId(partsId)))
                error += "제조사코드는 이미 존재합니다 [" + partsId + "]\n";
            CommonBikes commonCodeBikesById = bikeWorker.getCommonCodeBikesById(carModel);
            if(!bePresent(commonCodeBikesById))
                error += "차량은 존재하지않습니다.\n";
            PartsCodes byPartsName = partsCodesRepository.findByPartsName(partsName);
            if(!bePresent(byPartsName))
                error += "부품명 " + partsName + " 이 없습니다.\n";
            if(!error.equals(init)){
                errors += error;
            }else{
                partsRepository.save(new Parts(partsId, byPartsName.getPartsCodeNo(), partsPrice, workingHour, commonCodeBikesById.getCode()));
            }
        }
        if(!errors.equals("")){
//            throw new RuntimeException(errors);
            writeMessage(errors);
        }
        return request;
    }
}
