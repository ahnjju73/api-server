package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bike.Diagrams;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagramsRepository extends JpaRepository<Diagrams, Integer> {

}
