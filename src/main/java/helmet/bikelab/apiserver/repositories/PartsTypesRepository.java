package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.PartsTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartsTypesRepository extends JpaRepository<PartsTypes, Integer> {
    List<PartsTypes> findAllBy();
    PartsTypes findTop1ByOrderByOrderNoDesc();
    PartsTypes findByPartsTypeNo(Integer partsTypeNo);
    PartsTypes findByPartsType(String partsType);
}
