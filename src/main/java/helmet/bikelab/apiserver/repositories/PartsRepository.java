package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.Parts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartsRepository extends JpaRepository<Parts, Long> {

    Parts findByPartNo(Long partsNo);
    Parts findByPartsId(String partsId);
    Parts findByPartNoAndBikeModelCode(Long partsNo, String carModel);
    List<Parts> findAllByBikeModelCode(String bikeModelCode);
}
