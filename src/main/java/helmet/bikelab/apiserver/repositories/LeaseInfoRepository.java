package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeaseInfo;
import org.hibernate.metamodel.model.convert.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaseInfoRepository extends JpaRepository<LeaseInfo, Integer> {

}
