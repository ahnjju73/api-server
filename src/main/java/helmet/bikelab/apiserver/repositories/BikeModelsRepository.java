package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeModelsRepository extends JpaRepository<CommonCodeBikes, String> {
    List<CommonCodeBikes> findAllByVolume(Double volume);
    CommonCodeBikes findByCode(String code);
    CommonCodeBikes findByModel(String model);
}
