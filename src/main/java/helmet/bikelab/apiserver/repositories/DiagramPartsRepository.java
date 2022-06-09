package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.DiagramParts;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.domain.types.DiagramPartsPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagramPartsRepository extends JpaRepository<DiagramParts, DiagramPartsPK> {

    void deleteByDiagramNo(Integer diagramNo);
    DiagramParts findByDiagramNoAndPartNo(Integer diagramNo, Long partsNo);
    void deleteByDiagramNoAndPartNo(Integer diagramNo, Long partsNo);
    List<DiagramParts> findAllByDiagramNo(Integer diagramNo);
    Page<DiagramParts> findAllByOrderByDiagram_Name(Pageable pageable);
    DiagramParts findTop1ByDiagramNoOrderByOrderNoDesc(Integer diagramNo);
    List<DiagramParts> findAllByDiagramNoOrderByOrderNo(Integer diagramNo);

}
