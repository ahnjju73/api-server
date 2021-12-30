package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.bike.PartsImages;
import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import helmet.bikelab.apiserver.domain.types.UnitTypes;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.BikeModelsRepository;
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

    public BikeSessionRequest fetchPartImageByPartsId(BikeSessionRequest request){
        PartsByIdRequest partsByIdRequest = map(request.getParam(), PartsByIdRequest.class);
        Parts partsById = bikeWorker.getPartsById(partsByIdRequest.getPartsNo());
        List<PartsImages> images = partsById.getImages();
        request.setResponse(!bePresent(images) ? new ArrayList() : images);
        return request;
    }

    @Transactional
    public BikeSessionRequest deletePartsImage(BikeSessionRequest request){
        DeletePartsImageRequest partsByIdRequest = map(request.getParam(), DeletePartsImageRequest.class);
        Parts partsById = bikeWorker.getPartsById(partsByIdRequest.getPartsNo());
        List<PartsImages> images = partsById.getImages();
        if(bePresent(images)){
            for(int i = 0; i < images.size(); i++){
                PartsImages partsImages = images.get(i);
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
            List<PartsImages> images = partsByIdAndCarModel.getImages();
            List<PartsImages> collect = addPartsImageRequest
                    .getImages()
                    .stream().map(presignedURLVo -> {
                        AmazonS3 amazonS3 = AmazonS3Client.builder()
                                .withCredentials(AmazonUtils.awsCredentialsProvider())
                                .build();
                        String fileKey = "parts/" + partsByIdAndCarModel.getPartNo() + "/" + presignedURLVo.getFileKey();
                        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                        amazonS3.copyObject(objectRequest);
                        PartsImages partsImage = new PartsImages();
                        partsImage.setMediaType(MediaTypes.IMAGE.getStatus());
                        partsImage.setFileName(presignedURLVo.getFilename());
                        partsImage.setUri("/" + fileKey);
                        partsImage.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
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
        String extension = (String)param.get("extension");
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
        BikePartsDto bikePartsDto = map(param, BikePartsDto.class);
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
        if(!partsUpdatedRequest.getWorkingPrices().equals(partsByIdAndCarModel.getWorkingPrices())){
            partsByIdAndCarModel.setWorkingPrices(partsUpdatedRequest.getWorkingPrices());
            changed = true;
        }
        if(!partsUpdatedRequest.getWorkingHours().equals(partsByIdAndCarModel.getWorkingHours())){
            partsByIdAndCarModel.setWorkingHours(partsUpdatedRequest.getWorkingHours());
            changed = true;
        }
        if(!partsUpdatedRequest.getUnits().equals(partsByIdAndCarModel.getUnits())){
            partsByIdAndCarModel.setUnits(partsUpdatedRequest.getUnits());
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
        CommonCodeBikes model = bikeWorker.getCommonCodeBikesById(bikePartsDto.getCarModel());
        PartsCodes partsCodes = bikeWorker.getPartsCodeById(bikePartsDto.getPartsCodeNo());
        parts.setPartsId(bikePartsDto.getPartsId());
        parts.setBikeModelCode(model.getCode());
        parts.setPartsCodeNo(partsCodes.getPartsCodeNo());
        parts.setPartsPrices(bikePartsDto.getPartsPrices());
        parts.setWorkingHours(bikePartsDto.getWorkingHours());
        parts.setWorkingPrices(bikePartsDto.getWorkingPrices());
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

}
