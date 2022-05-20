package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CommonBikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeModelsRepository extends JpaRepository<CommonBikes, String> {
    List<CommonBikes> findAllByVolume(Double volume);
    CommonBikes findByCode(String code);
    CommonBikes findByModel(String model);

    List<CommonBikes> findByManufacturerNo(Integer manufacturerNo);
    void deleteByCode(String code);

}
