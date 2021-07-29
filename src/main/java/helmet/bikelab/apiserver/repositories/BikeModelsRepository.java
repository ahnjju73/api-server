package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CommonCodeBikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BikeModelsRepository extends JpaRepository<CommonCodeBikes, String> {
    CommonCodeBikes findByCode(String code);
    CommonCodeBikes findByModel(String model);
}
