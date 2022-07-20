package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.SectionAxis;
import helmet.bikelab.apiserver.domain.SectionAxisParts;
import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.SectionWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BikeModelByImageService extends SessService {

    private final CommonWorker commonWorker;
    private final BikeWorker bikeWorker;
    private final SectionsRepository sectionsRepository;
    private final SectionWorker sectionWorker;
    private final SectionAxisRepository sectionAxisRepository;
    private final SectionAxisPartsRepository sectionAxisPartsRepository;

    public SessionRequest generatePreSignedUrl(SessionRequest request){
        Map param = request.getParam();
        String filename = (String)param.get("filename");
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(filename, null);
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public SessionRequest doSaveSection(SessionRequest request){
        NewSectionRequest newSectionRequest = map(request.getParam(), NewSectionRequest.class);
        CommonBikes commonCodeBikesById = bikeWorker.getCommonCodeBikesById(newSectionRequest.getCarModel());
        List<ImageVo> collect = newSectionRequest.getImages().stream().map(elm -> {
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            String fileKey = "parts_by_image/" + elm.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(elm.getBucket(), elm.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            ImageVo partsImage = new ImageVo(MediaTypes.IMAGE, elm.getFilename(), fileKey);
            return partsImage;
        }).collect(Collectors.toList());
        Sections sections = new Sections(newSectionRequest.getSectionName(), commonCodeBikesById, collect);
        sectionsRepository.save(sections);
        return request;
    }

    @Transactional
    public SessionRequest doSaveSectionAxis(SessionRequest request){
        SectionAxisRequest sectionAxisRequest = map(request.getParam(), SectionAxisRequest.class);
        sectionAxisRequest.checkValidation();
        Sections sectionById = sectionWorker.getSectionById(sectionAxisRequest.getSectionNo());
        SectionAxis sectionAxis = new SectionAxis(sectionById, sectionAxisRequest.getName(), sectionAxisRequest.getAxis());
        sectionAxisRepository.save(sectionAxis);
        request.setResponse(sectionAxis);
        return request;
    }

    @Transactional
    public SessionRequest doUpdateSectionAxis(SessionRequest request){
        SectionAxisByIdRequest sectionAxisByIdRequest = map(request.getParam(), SectionAxisByIdRequest.class);
        SectionAxisRequest sectionAxisRequest = map(request.getParam(), SectionAxisRequest.class);
        sectionAxisRequest.checkValidation();
        SectionAxis bySectionNoAndAxisNo = sectionWorker.getSectionAxisBySectionNoAndAxisNo(sectionAxisRequest.getSectionNo(), sectionAxisByIdRequest.getAxisNo());
        bySectionNoAndAxisNo.updateInfo(sectionAxisRequest);
        sectionAxisRepository.save(bySectionNoAndAxisNo);
        request.setResponse(bySectionNoAndAxisNo);
        return request;
    }

    @Transactional
    public SessionRequest handleSectionAxisParts(SessionRequest request){
        SectionAxisByIdRequest sectionAxisByIdRequest = map(request.getParam(), SectionAxisByIdRequest.class);
        BikePartsRequest bikePartsRequest = map(request.getParam(), BikePartsRequest.class);
        SectionAxis sectionAxisByAxisNo = sectionWorker.getSectionAxisByAxisNo(sectionAxisByIdRequest.getAxisNo());
        Parts partsById = bikeWorker.getPartsByPartsIdAndCarModel(bikePartsRequest.getPartsId(), bikePartsRequest.getCarModel());
        SectionAxisParts byAxisNoAndPartsNo = sectionAxisPartsRepository.findByAxisNoAndPartsNo(sectionAxisByIdRequest.getAxisNo(), partsById.getPartNo());
        if(bePresent(byAxisNoAndPartsNo)){
            sectionAxisPartsRepository.delete(byAxisNoAndPartsNo);
        }else {
            SectionAxisParts sectionAxisParts = new SectionAxisParts(sectionAxisByAxisNo, partsById);
            sectionAxisPartsRepository.save(sectionAxisParts);
        }

        return request;
    }

    public SessionRequest fetchSectionsByModel(BikeSessionRequest request) {
        SectionsFetchRequest sectionsFetchRequest = map(request.getParam(), SectionsFetchRequest.class);
        request.setResponse(sectionWorker.getSectionsByBikeModel(sectionsFetchRequest.getBikeModel()));
        return request;
    }

    public SessionRequest fetchSectionAxisDetail(BikeSessionRequest request) {
        SectionsFetchRequest sectionsFetchRequest = map(request.getParam(), SectionsFetchRequest.class);
        request.setResponse(sectionWorker.fetchSectionDetail(sectionsFetchRequest.getSectionNo()));
        return request;
    }

    public SessionRequest fetchPartsByAxis(BikeSessionRequest request) {
        SectionsFetchRequest sectionsFetchRequest = map(request.getParam(), SectionsFetchRequest.class);
        request.setResponse(sectionWorker.getPartsBySectionAxis(sectionsFetchRequest.getAxisNo()));
        return request;
    }

    @Transactional
    public SessionRequest deleteSectionAxis(BikeSessionRequest request){
        SectionsFetchRequest sectionsFetchRequest = map(request.getParam(), SectionsFetchRequest.class);
        sectionWorker.deleteSectionAxis(sectionsFetchRequest.getAxisNo());
        return request;
    }

    @Transactional
    public SessionRequest deleteSection(BikeSessionRequest request){
        SectionsFetchRequest sectionsFetchRequest = map(request.getParam(), SectionsFetchRequest.class);
        sectionWorker.deleteSection(sectionsFetchRequest.getSectionNo());
        return request;
    }

    public SessionRequest doUpdateSection(BikeSessionRequest request) {
        NewSectionRequest newSectionRequest = map(request.getParam(), NewSectionRequest.class);
        Sections section = sectionWorker.getSectionById(newSectionRequest.getSectionNo());
        CommonBikes commonCodeBikesById = bikeWorker.getCommonCodeBikesById(newSectionRequest.getCarModel());
        List<ImageVo> collect = newSectionRequest.getImages().stream().map(elm -> {
            if(!bePresent(elm.getBucket())){
                return section.getImageList().get(0);
            }
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            String fileKey = "parts_by_image/" + elm.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(elm.getBucket(), elm.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            ImageVo partsImage = new ImageVo(MediaTypes.IMAGE, elm.getFilename(), fileKey);
            return partsImage;
        }).collect(Collectors.toList());
        section.setCarModelCode(commonCodeBikesById.getCode());
        section.setSectionName(newSectionRequest.getSectionName());
        section.setImageList(collect);
        sectionsRepository.save(section);
        return request;
    }
}
