package helmet.bikelab.apiserver.services.leases;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.FineDto;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasesDto;
import helmet.bikelab.apiserver.objects.requests.AddFineAttachmentRequest;
import helmet.bikelab.apiserver.objects.requests.AddUpdateFineRequest;
import helmet.bikelab.apiserver.objects.requests.FetchFineRequest;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.FinesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.FineWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FineService extends SessService {

    private final FinesRepository finesRepository;
    private final FineWorker fineWorker;
    private final AutoKey autoKey;
    private final CommonWorker commonWorker;

    public BikeSessionRequest fetchFineList(BikeSessionRequest request){
        Map param = request.getParam();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(fetchFineRequest, "leases.fines.fetchAllFines", "leases.fines.countAllFines", "rownum");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchFineDetail(BikeSessionRequest request){
        Map param = request.getParam();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        Fines fineById = fineWorker.getFineById(fetchFineRequest.getFineId());
        request.setResponse(fineById);
        return request;
    }

    @Transactional
    public BikeSessionRequest addFine(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateFineRequest addUpdateFineRequest = map(param, AddUpdateFineRequest.class);
        addUpdateFineRequest.checkValidation();
        Fines fines = fineWorker.makeNewFine(addUpdateFineRequest);
        finesRepository.save(fines);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateFine(BikeSessionRequest request){
        Map param = request.getParam();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        AddUpdateFineRequest addUpdateFineRequest = map(param, AddUpdateFineRequest.class);
        addUpdateFineRequest.checkValidation();
        Fines fineById = fineWorker.getFineById(fetchFineRequest.getFineId());
        Fines fines = fineWorker.setFine(addUpdateFineRequest, fineById);
        finesRepository.save(fines);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteFine(BikeSessionRequest request){
        Map param = request.getParam();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        Fines fineById = fineWorker.getFineById(fetchFineRequest.getFineId());
        fineById.setDeletedAt(LocalDateTime.now());
        finesRepository.save(fineById);
        return request;
    }

    public BikeSessionRequest generatePresignedUrl(BikeSessionRequest request){
        Map param = request.getParam();
        FineDto fineDto = map(param, FineDto.class);
        String filename = fineDto.getFilename().substring(0, fineDto.getFilename().lastIndexOf("."));
        String extension = fineDto.getFilename().substring(fineDto.getFilename().lastIndexOf(".") + 1);
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(filename, extension);
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public BikeSessionRequest addAttachments(BikeSessionRequest request){
        Map param = request.getParam();
        AddFineAttachmentRequest addFineAttachmentRequest = map(param, AddFineAttachmentRequest.class);
        Fines fine = fineWorker.getFineById(addFineAttachmentRequest.getFineId());
        List<ModelAttachment> attachmentsList = fine.getAttachmentsList();
        if(!bePresent(attachmentsList))
            attachmentsList = new ArrayList<>();
        List<ModelAttachment> attachments = addFineAttachmentRequest.getAttachments()
                .stream().map(presignedURLVo -> {
                    AmazonS3 amazonS3 = AmazonS3Client.builder()
                            .withRegion(Regions.AP_NORTHEAST_2)
                            .withCredentials(AmazonUtils.awsCredentialsProvider())
                            .build();
                    String fileKey = "fine-attachment/" + fine.getFineNo() + "/" + presignedURLVo.getFileKey();
                    CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                    amazonS3.copyObject(objectRequest);
                    ModelAttachment leaseAttachment = new ModelAttachment();
                    leaseAttachment.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
                    leaseAttachment.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                    leaseAttachment.setUri("/" + fileKey);
                    leaseAttachment.setFileName(presignedURLVo.getFilename());
                    return leaseAttachment;
                }).collect(Collectors.toList());
        attachmentsList.addAll(attachments);
        fine.setAttachmentsList(attachmentsList);
        finesRepository.save(fine);
        return request;
    }
}
