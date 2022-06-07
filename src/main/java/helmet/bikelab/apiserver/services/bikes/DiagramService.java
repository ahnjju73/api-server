package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.EstimateParts;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.repositories.DiagramPartsRepository;
import helmet.bikelab.apiserver.repositories.DiagramsRepository;
import helmet.bikelab.apiserver.repositories.EstimatePartsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
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

@RequiredArgsConstructor
@Service
public class DiagramService extends SessService {

    private final BikeWorker bikeWorker;
    private final DiagramsRepository diagramsRepository;
    private final DiagramPartsRepository diagramPartsRepository;
    private final DiagramWorker diagramWorker;
    private final EstimatePartsRepository estimatePartsRepository;
    private final CommonWorker commonWorker;

    @Transactional
    public BikeSessionRequest addNewDiagram(BikeSessionRequest request){
        DiagramInfoRequest diagramInfoRequest = map(request.getParam(), DiagramInfoRequest.class);
        diagramInfoRequest.checkValidation();
        bikeWorker.getCommonCodeBikesById(diagramInfoRequest.getCarModel());
        Diagrams diagrams = new Diagrams(diagramInfoRequest);
        diagramsRepository.save(diagrams);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateDiagram(BikeSessionRequest request){
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteDiagram(BikeSessionRequest request){
        DiagramByIdRequest diagramByIdRequest = map(request.getParam(), DiagramByIdRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramByIdRequest.getDiagramId());
        List<EstimateParts> allByDiagramPartsDiagramNo = estimatePartsRepository.findAllByDiagramParts_DiagramNo(diagramById.getDiagramNo());
        if(bePresent(allByDiagramPartsDiagramNo)){
            diagramById.setDeletedAt(LocalDateTime.now());
            diagramsRepository.save(diagramById);
        }else {
            diagramPartsRepository.deleteByDiagramNo(diagramById.getDiagramNo());
            diagramsRepository.deleteByDiagramNo(diagramById.getDiagramNo());
        }
         return request;
    }

    public BikeSessionRequest fetchDiagramDetailsById(BikeSessionRequest request){
        DiagramByIdAndCarModelRequest diagramByIdAndCarModelRequest = map(request.getParam(), DiagramByIdAndCarModelRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramByIdAndCarModelRequest.getDiagramId());
        request.setResponse(diagramById);
        return request;
    }

    public BikeSessionRequest fetchAllDiagramList(BikeSessionRequest request){
        PageableRequest pageableRequest = map(request.getParam(), PageableRequest.class);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        DiagramByIdAndCarModelRequest diagramFetchAllByIdRequest = map(request.getParam(), DiagramByIdAndCarModelRequest.class);
        if(bePresent(diagramFetchAllByIdRequest.getCarModel())){
            Page<Diagrams> allBy = diagramsRepository.findAllByCarModelCode(diagramFetchAllByIdRequest.getCarModel(), pageable);
            request.setResponse(allBy);
        }else {
            Page<Diagrams> allBy = diagramsRepository.findAllBy(pageable);
            request.setResponse(allBy);
        }
        return request;
    }

    public BikeSessionRequest generatePreSigned(BikeSessionRequest request){
        Map param = request.getParam();
        String filename = (String)param.get("filename");
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl("diagrams/" + filename, null);
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateImageByDiagramId(BikeSessionRequest request){
        DiagramImageByIdRequest diagramImageByIdRequest = map(request.getParam(), DiagramImageByIdRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramImageByIdRequest.getDiagramId());
        diagramById.setImageList(diagramImageByIdRequest.getImages());
        diagramsRepository.save(diagramById);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteImageByDiagramId(BikeSessionRequest request){
        DiagramImageDeleteByIdRequest diagramByIdRequest = map(request.getParam(), DiagramImageDeleteByIdRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramByIdRequest.getDiagramId());
        diagramById.deleteImageById(diagramById.getDiagramId());
        diagramsRepository.save(diagramById);
        return request;
    }


}
