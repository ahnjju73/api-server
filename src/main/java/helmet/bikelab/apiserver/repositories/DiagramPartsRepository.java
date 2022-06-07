package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.DiagramParts;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import helmet.bikelab.apiserver.domain.types.DiagramPartsPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagramPartsRepository extends JpaRepository<DiagramParts, DiagramPartsPK> {

}
