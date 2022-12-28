package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.PartsPriceBak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartsPriceBakRepository extends JpaRepository<PartsPriceBak, Long> {
    List<PartsPriceBak> findAllByPartsNoOrderByBakNoDesc(Long partsNo);

    Integer countByPartsNo(Long partsNo);
}
