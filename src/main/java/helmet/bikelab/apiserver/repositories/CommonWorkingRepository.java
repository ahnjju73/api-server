package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.CommonWorking;
import helmet.bikelab.apiserver.domain.types.BikeTypes;
import helmet.bikelab.apiserver.domain.types.CommonWorkingPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommonWorkingRepository extends JpaRepository<CommonWorking, CommonWorkingPK> {
    List<CommonWorking> findByBikeTypeOrderByVolumeAsc(BikeTypes bikeType);
    List<CommonWorking> findByBikeType(BikeTypes bikeTypes);

}
