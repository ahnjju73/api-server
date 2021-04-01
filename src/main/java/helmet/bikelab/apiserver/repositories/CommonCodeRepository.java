package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CommonCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommonCodeRepository extends JpaRepository<CommonCode, String> {
    CommonCode findByCodeName(String codeName);
    List<CommonCode> findByUpperCode(String upperCode);

}
