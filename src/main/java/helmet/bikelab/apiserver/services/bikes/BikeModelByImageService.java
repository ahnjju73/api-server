package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.EstimateParts;
import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.domain.bike.DiagramParts;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.domain.bike.ImageVo;
import helmet.bikelab.apiserver.domain.types.MediaTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.SessionRequest;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.repositories.DiagramPartsRepository;
import helmet.bikelab.apiserver.repositories.DiagramsRepository;
import helmet.bikelab.apiserver.repositories.EstimatePartsRepository;
import helmet.bikelab.apiserver.repositories.SectionsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.DiagramWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BikeModelByImageService extends SessService {

    private final CommonWorker commonWorker;
    private final BikeWorker bikeWorker;
    private final SectionsRepository sectionsRepository;

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
            AmazonS3 amazonS3 = AmazonS3Client.builder()
                    .withCredentials(AmazonUtils.awsCredentialsProvider())
                    .build();
            String fileKey = "parts_by_image/" + elm.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(elm.getBucket(), elm.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            ImageVo partsImage = new ImageVo(MediaTypes.IMAGE, elm.getFilename(), fileKey);
            return partsImage;
        }).collect(Collectors.toList());
        Sections sections = new Sections(commonCodeBikesById, collect);
        sectionsRepository.save(sections);
        request.setResponse(sections);
        return request;
    }
}
