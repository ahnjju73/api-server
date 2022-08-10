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
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.objects.responses.FetchFineDetailResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.FinesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.*;
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
    private final ClientWorker clientWorker;
    private final RiderWorker riderWorker;
    private final BikeWorker bikeWorker;
    private final CommonWorker commonWorker;

    public BikeSessionRequest fetchFineList(BikeSessionRequest request){
        Map param = request.getParam();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(fetchFineRequest, "leases.fines.fetchAllFines", "leases.fines.countAllFines", "fine_id");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchFineDetail(BikeSessionRequest request){
        Map param = request.getParam();
        FetchFineRequest fetchFineRequest = map(param, FetchFineRequest.class);
        Fines fineById = fineWorker.getFineById(fetchFineRequest.getFineId());
        FetchFineDetailResponse fetchFineDetailResponse = fineWorker.getFineInfo(fineById);
        request.setResponse(fetchFineDetailResponse);
        return request;
    }

    @Transactional
    public BikeSessionRequest addFine(BikeSessionRequest request){
        Map param = request.getParam();
        AddUpdateFineRequest addUpdateFineRequest = map(param, AddUpdateFineRequest.class);
        addUpdateFineRequest.checkValidation();
//        if(bePresent(finesRepository.findByFineNum(addUpdateFineRequest.getFineNum())))
//            withException("710-009");
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
//        Fines byFineNum = finesRepository.findByFineNum(addUpdateFineRequest.getFineNum());
//        if(bePresent(byFineNum) && !byFineNum.getFineId().equals(fetchFineRequest.getFineId()))
//            withException("710-009");
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
        PresignedURLVo presignedURLVo;
        if(fineDto.getFilename().indexOf(".") >= 0) {
            String filename = fineDto.getFilename().substring(0, fineDto.getFilename().lastIndexOf("."));
            String extension = fineDto.getFilename().substring(fineDto.getFilename().lastIndexOf(".") + 1);
            presignedURLVo = commonWorker.generatePreSignedUrl(filename, extension);
        }else
            presignedURLVo = commonWorker.generatePreSignedUrl(fineDto.getFilename(), null);
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
                    AmazonS3 amazonS3 = AmazonUtils.amazonS3();
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

    public BikeSessionRequest fetchAttachments(BikeSessionRequest request){
        Map param = request.getParam();
        FetchFineRequest fineRequest = map(param, FetchFineRequest.class);
        Fines fine = fineWorker.getFineById(fineRequest.getFineId());
        request.setResponse(fine.getAttachmentsList() == null ? new ArrayList<>() : fine.getAttachmentsList());
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteAttachment(BikeSessionRequest request) {
        Map param = request.getParam();
        DeleteFineAttachmentRequest deleteFineAttachmentRequest = map(param, DeleteFineAttachmentRequest.class);
        Fines fine = fineWorker.getFineById(deleteFineAttachmentRequest.getFineId());
        fine = fineWorker.removeAttachment(fine, deleteFineAttachmentRequest.getUuid());
        finesRepository.save(fine);
        return request;
    }


    public BikeSessionRequest addFinesByExcel(BikeSessionRequest request){
        AddUpdateFineExcelRequest addUpdateFineExcelRequest = map(request.getParam(), AddUpdateFineExcelRequest.class);
        addUpdateFineExcelRequest.validationCheck();
        for(AddUpdateFineRequest item : addUpdateFineExcelRequest.getFines()){
            Fines fines = fineWorker.setFine(item);
            finesRepository.save(fines);
        }
        return request;
    }
}
