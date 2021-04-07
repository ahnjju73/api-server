package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuranceOptionlRepository extends JpaRepository<CommonCodeInsurances, String> {
    List<CommonCodeInsurances> findByUpperCode(String upper);
}
