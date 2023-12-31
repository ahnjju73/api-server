package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.Parts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartsRepository extends JpaRepository<Parts, Long> {

    Parts findByPartNo(Long partsNo);
    Parts findByPartsId(String partsId);
    Boolean existsByPartsId(String partsId);
    Parts findByPartNoAndBikeModelCode(Long partsNo, String carModel);
    Parts findByPartsIdAndBikeModelCode(String partsId, String carModel);
    Parts findByBikeModelCodeAndPartsCodeNo(String carModel, Integer partCodeNo);
    List<Parts> findAllByBikeModelCode(String bikeModelCode);
    List<Parts> findAllByPartNoIn(List<Long> partsNoIn);
}
