package helmet.bikelab.apiserver.services.bikes;

import helmet.bikelab.apiserver.domain.bike.DiagramParts;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.DiagramByIdRequest;
import helmet.bikelab.apiserver.objects.requests.DiagramPartsByIdRequest;
import helmet.bikelab.apiserver.objects.requests.DiagramPartsRemovedByIdRequest;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.repositories.DiagramPartsRepository;
import helmet.bikelab.apiserver.repositories.PartsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.DiagramWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DiagramPartsService extends SessService {

    private final DiagramWorker diagramWorker;
    private final PartsRepository partsRepository;
    private final DiagramPartsRepository diagramPartsRepository;

    @Transactional
    public BikeSessionRequest addPartsByDiagramId(BikeSessionRequest request){
        DiagramPartsByIdRequest diagramPartsByIdRequest = map(request.getParam(), DiagramPartsByIdRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramPartsByIdRequest.getDiagramId());
        List<Parts> allByPartNoIn = partsRepository.findAllByPartNoIn(diagramPartsByIdRequest.getParts());
        DiagramParts top1ByDiagramNoOrderByOrderNo = diagramPartsRepository.findTop1ByDiagramNoOrderByOrderNoDesc(diagramById.getDiagramNo());
        AtomicInteger count = new AtomicInteger(0);
        if(bePresent(top1ByDiagramNoOrderByOrderNo)){
            count.set(top1ByDiagramNoOrderByOrderNo.getOrderNo() + 1);
        }
        if(bePresent(allByPartNoIn)){
            List<DiagramParts> collect = allByPartNoIn.stream().map(elm -> {
                DiagramParts diagramParts = new DiagramParts(diagramById, elm, count.get());
                count.getAndIncrement();
                return diagramParts;
            }).collect(Collectors.toList());
            diagramPartsRepository.saveAll(collect);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest reorderDiagramParts(BikeSessionRequest request){
        DiagramPartsByIdRequest diagramPartsByIdRequest = map(request.getParam(), DiagramPartsByIdRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramPartsByIdRequest.getDiagramId());
        List<DiagramParts> allByDiagramNo = diagramPartsRepository.findAllByDiagramNoOrderByOrderNo(diagramById.getDiagramNo());
        AtomicInteger count = new AtomicInteger(0);
        allByDiagramNo.forEach(elm -> {
            elm.setOrderNo(count.get());
            count.incrementAndGet();
        });
        diagramPartsRepository.saveAll(allByDiagramNo);
        return request;
    }

    @Transactional
    public BikeSessionRequest removePartsByDiagramId(BikeSessionRequest request){
        DiagramPartsRemovedByIdRequest diagramPartsRemovedByIdRequest = map(request.getParam(), DiagramPartsRemovedByIdRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramPartsRemovedByIdRequest.getDiagramId());
        diagramPartsRepository.deleteByDiagramNoAndPartNo(diagramById.getDiagramNo(), diagramPartsRemovedByIdRequest.getPartsNo());
        return request;
    }

    public BikeSessionRequest fetchPartListByDiagramId(BikeSessionRequest request){
        DiagramByIdRequest diagramByIdRequest = map(request.getParam(), DiagramByIdRequest.class);
        Diagrams diagramById = diagramWorker.getDiagramById(diagramByIdRequest.getDiagramId());
        List<DiagramParts> allByDiagramNo = diagramPartsRepository.findAllByDiagramNo(diagramById.getDiagramNo());
        request.setResponse(allByDiagramNo);
        return request;
    }

    public BikeSessionRequest fetchAllPartListOfDiagramId(BikeSessionRequest request){
        PageableRequest pageableRequest = map(request.getParam(), PageableRequest.class);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        Page<DiagramParts> allBy = diagramPartsRepository.findAllByOrderByDiagram_Name(pageable);
        request.setResponse(allBy);
        return request;
    }
}
