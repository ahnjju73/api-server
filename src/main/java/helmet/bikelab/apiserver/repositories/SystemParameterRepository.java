package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.SystemParameter;
import helmet.bikelab.apiserver.services.SystemService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemParameterRepository extends JpaRepository<SystemParameter, Integer> {
    SystemParameter findByRemark(String remark);
}
