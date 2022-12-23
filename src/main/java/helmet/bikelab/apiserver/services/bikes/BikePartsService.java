package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.CommonWorking;
import helmet.bikelab.apiserver.domain.SampleFiles;
import helmet.bikelab.apiserver.domain.bike.*;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BikePartsService extends SessService {

    private final BikeModelsRepository bikeModelsRepository;
    private final CommonWorker commonWorker;
    private final PartsRepository partsRepository;
    private final BikeWorker bikeWorker;
    private final PartsTypesRepository partsTypesRepository;
    private final CommonWorkingRepository commonWorkingRepository;
    private final PartsCodesRepository partsCodesRepository;
    private final PartsPriceBakRepository partsPriceBakRepository;
    private final SampleFilesRepository sampleFilesRepository;
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
        String extension = (String)param.get("extension");
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(filename, extension);
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
        if(!partsUpdatedRequest.getMerchantId().equals(partsByIdAndCarModel.getMerchantId())){
            partsByIdAndCarModel.setMerchantId(partsUpdatedRequest.getMerchantId());
//            changed = true;
        }
        if(!partsUpdatedRequest.getPartsId().equals(partsByIdAndCarModel.getPartsId())){
            partsByIdAndCarModel.setPartsId(partsUpdatedRequest.getPartsId());
//            changed = true;
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
            BikeUser sessionUser = request.getSessionUser();
            PartsPriceBak partsPriceBak = new PartsPriceBak();
            partsPriceBak.initialized(partsByIdAndCarModel, sessionUser);
            partsPriceBak.setDescription("수기변경");
            partsPriceBakRepository.save(partsPriceBak);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest addPartsByModel(BikeSessionRequest request) {
        Map param = request.getParam();
        BikeUser sessionUser = request.getSessionUser();
        PartsNewRequest bikePartsDto = map(param, PartsNewRequest.class);
        bikePartsDto.checkValidation();
        Parts parts = new Parts();
        CommonBikes model = bikeWorker.getCommonCodeBikesById(bikePartsDto.getCarModel());
        PartsCodes partsCodes = bikeWorker.getPartsCodeById(bikePartsDto.getPartsCodeNo());
        parts.setMerchantId(bikePartsDto.getMerchantId());
        parts.setPartsId(bikePartsDto.getPartsId());
        parts.setBikeModelCode(model.getCode());
        parts.setPartsCodeNo(partsCodes.getPartsCodeNo());
        parts.setPartsPrices(bikePartsDto.getPartsPrices());
        parts.setWorkingHours(bikePartsDto.getWorkingHours());
        parts.setUnits(bikePartsDto.getUnits());
        partsRepository.save(parts);

        PartsPriceBak partsPriceBak = new PartsPriceBak();
        partsPriceBak.initialized(parts, sessionUser);
        partsPriceBakRepository.save(partsPriceBak);

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
        List<PartsCodeUploadRequest> parts = partsExcelRequest.getParts();
        String errors = "";
        for (int i = 0; i < parts.size(); i++){
            String index = (i + 2) + "번째 오류\n";
            String errorText = "";
            PartsCodeUploadRequest partsCodeRow = parts.get(i);
            if(!bePresent(partsCodeRow.getPartsNameEng())) errorText += "부품(영문)명이 존재하지않습니다.\n";
            PartsCodes partsCodeById = partsCodesRepository.findByPartsCodeNo(partsCodeRow.getPartsCodeNo());
            if(bePresent(partsCodeById)){
                // 수정하기
                updatePartsCodeByExcel(partsCodeRow, partsCodeById, errorText);
            }else {
                // 신규등록
                errorText = addPartsCodeByExcel(partsCodeRow, errorText);
            }
            if(bePresent(errorText)){
                errors += (index + errorText);
            }
        }
        if(bePresent(errors)){
            writeMessage(errors);
        }
        return request;
    }

    private String addPartsCodeByExcel(PartsCodeUploadRequest partsCodeRow, String errorText) {
        PartsTypes partsType = partsTypesRepository.findByPartsTypeNo(partsCodeRow.getPartsTypeNo());
        if(!bePresent(partsType)) {
            errorText += "계통정보가 없습니다\n";
            return errorText;
        }
        if(!bePresent(errorText)){
            PartsCodes partsCode = new PartsCodes(partsType, partsCodeRow.getPartsName(), partsCodeRow.getPartsNameEng());
            partsCodesRepository.save(partsCode);
        }
        return errorText;

    }

    private void updatePartsCodeByExcel(PartsCodeUploadRequest partsCodeRow, PartsCodes partsCodeById, String errorText) {
        if(!bePresent(errorText)){
            partsCodeById.setPartsName(partsCodeRow.getPartsName());
            partsCodeById.setPartsNameEng(partsCodeRow.getPartsNameEng());
            partsCodesRepository.save(partsCodeById);
        }
    }

    @Transactional
    public BikeSessionRequest uploadModelPartsPrice(BikeSessionRequest request) {
        BikeUser sessionUser = request.getSessionUser();
        UploadPartsPriceRequest uploadPartsPriceRequest = map(request.getParam(), UploadPartsPriceRequest.class);
        PresignedURLVo pre = uploadPartsPriceRequest.getFile();
        SampleFiles sampleFiles = new SampleFiles();
        if(bePresent(pre)){
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            String fileKey = "sample-files/" + LocalDate.now() + "/" + UUID.randomUUID().toString().replace("-", "") + "/" + pre.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(pre.getBucket(), pre.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            sampleFiles.setLinks(ENV.AWS_S3_ORIGIN_DOMAIN + "/" + fileKey);
            sampleFilesRepository.save(sampleFiles);
        }
        List<UploadPartsPrice> partsPriceList = uploadPartsPriceRequest.getPartsPriceList();
        if(!bePresent(partsPriceList)) writeMessage("업데이트할 내용이 없습니다.");
        StringBuilder errors = new StringBuilder("");
        for(int i = 0; i < partsPriceList.size(); i++){
            Integer index = i + 2;
            Boolean hasError = false;
            UploadPartsPrice uploadPartsPrice = partsPriceList.get(i);
            Parts partsByPartsId = partsRepository.findByPartsId(uploadPartsPrice.getPartsId());
            if(!bePresent(partsByPartsId)){
                errors.append("[" + index + "번째] 부품 ID가 없습니다. [" + partsByPartsId + "]\n");
                hasError = true;
            }
            if(!bePresent(uploadPartsPrice.getPartsPrice())){
                errors.append("[" + index + "번째] 부품 가격이 없습니다. [" + uploadPartsPrice.getPartsPrice() + "]\n");
                hasError = true;
            }
            if(!bePresent(uploadPartsPrice.getWorkingHour())){
                errors.append("[" + index + "번째] 부품 가격이 없습니다. [" + uploadPartsPrice.getWorkingHour() + "]\n");
                hasError = true;
            }
            if(!hasError){
                partsByPartsId.setPartsPrices(uploadPartsPrice.getPartsPrice());
                partsByPartsId.setWorkingHours(uploadPartsPrice.getWorkingHour());
                partsRepository.save(partsByPartsId);
                PartsPriceBak partsPriceBak = new PartsPriceBak(partsByPartsId, uploadPartsPrice, uploadPartsPriceRequest.getDescription(), sampleFiles, sessionUser);
                partsPriceBakRepository.save(partsPriceBak);
            }else {
                Map response = new HashMap();
                response.put("error", errors);
                writeMessage(errors.toString());
            }

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
            String errorText = (i + 2) + "번째 열 오류.\n";
            BikePartsRequest bikePartsRequest = parts.get(i);
            String partsId = bikePartsRequest.getPartsId();
            Parts partByPartsId = partsRepository.findByPartsId(partsId);

            if(bePresent(partByPartsId)){
                // 수정
                updatePartsInfoByExcel(bikePartsRequest, partByPartsId);
            }else {
                // 신규등록
                BikeUser sessionUser = request.getSessionUser();
                errors = addNewPartsByExcel(errors, bikePartsRequest, sessionUser, errorText);
            }
        }
        if(bePresent(errors)){
            writeMessage(errors);
        }
        return request;
    }

    private String addNewPartsByExcel(String errors, BikePartsRequest bikePartsRequest, BikeUser sessionUser, String errorText) {
        String errorMessage = "";
        CommonBikes commonCodeBikesById = bikeModelsRepository.findByCode(bikePartsRequest.getCarModel());
        if(!bePresent(bikePartsRequest.getMerchantId())) errorMessage += "* 제조사코드가 존재하지않습니다.\n";
        if(!bePresent(bikePartsRequest.getPartsNameEng())) errorMessage += "* 부품(영문)명이 존재하지않습니다.\n";
        if(!bePresent(bikePartsRequest.getPartsPrice())) errorMessage += "* 부품가격이 존재하지않습니다.\n";
        if(!bePresent(bikePartsRequest.getWorkingHour())) errorMessage += "* 공임시간이 존재하지않습니다.\n";
        if(!bePresent(commonCodeBikesById)) errorMessage += "* 차량이 존재하지않습니다.\n";

        Integer partsNameEngCount = partsCodesRepository.countAllByPartsNameEng(bikePartsRequest.getPartsNameEng());
        if(partsNameEngCount > 1){
            List<PartsCodes> codes = partsCodesRepository.findAllByPartsNameEng(bikePartsRequest.getPartsNameEng());
            if(codes.size() > 0){
                String types = "";
                for(int j = 0; j <  codes.size(); j++){
                    types += j == codes.size() - 1 ? codes.get(j).getPartsType().getPartsType() : codes.get(j).getPartsType().getPartsType() + ", ";
                }
                errorMessage += "* 동일한 부품명이 해당 계통에 중복됩니다: [" + types + "]\n";
            }

        }else if(partsNameEngCount == 0) {
            errorMessage += "* 부품명 [" + bikePartsRequest.getPartsNameEng() + "] 이 없습니다.\n";
        }
        if(!bePresent(errorMessage)){
            PartsCodes partsCode = partsCodesRepository.findByPartsNameEng(bikePartsRequest.getPartsNameEng());
            Parts newParts = new Parts(bikePartsRequest, commonCodeBikesById, partsCode);
            partsRepository.save(newParts);
            PartsPriceBak partsPriceBak = new PartsPriceBak();
            partsPriceBak.initialized(newParts, sessionUser);
            partsPriceBakRepository.save(partsPriceBak);
        }else {
            errors += (errorText + errorMessage);
        }
        return errors;
    }

    private void updatePartsInfoByExcel(BikePartsRequest bikePartsRequest, Parts partByPartsId) {
        partByPartsId.setPartsPrices(bikePartsRequest.getPartsPrice());
        partByPartsId.setWorkingHours(bikePartsRequest.getWorkingHour());
        partByPartsId.setMerchantId(bikePartsRequest.getMerchantId());
        PartsCodes partsCode = partByPartsId.getPartsCode();
        partsCode.setPartsNameEng(bikePartsRequest.getPartsNameEng());
        partsCode.setPartsName(bikePartsRequest.getPartsName());
        partsCodesRepository.save(partsCode);
        partsRepository.save(partByPartsId);
    }

    public BikeSessionRequest getPartsPriceHistory(BikeSessionRequest request) {
        PartsByIdRequest partsByIdRequest = map(request.getParam(), PartsByIdRequest.class);
        Parts partsById = bikeWorker.getPartsById(partsByIdRequest.getPartsNo());
        List<PartsPriceBak> allByPartsNo = partsPriceBakRepository.findAllByPartsNoOrderByBakNoDesc(partsById.getPartNo());
        request.setResponse(bePresent(allByPartsNo) ? allByPartsNo : new ArrayList<>());
        return request;
    }

}
