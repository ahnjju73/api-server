package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.PartsCodes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartsCodesRepository extends JpaRepository<PartsCodes, Integer> {
    PartsCodes findByPartsCodeNo(Integer partCodeNo);
    List<PartsCodes> findByPartsTypeNo(Integer partsTypeNo);
    Page<PartsCodes> findAllByPartsNameContainingAndPartsType_PartsTypeContaining(String partsName, String partsType, Pageable pageable);
}
