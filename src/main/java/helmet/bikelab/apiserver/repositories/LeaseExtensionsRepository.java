package helmet.bikelab.apiserver.repositories;


import helmet.bikelab.apiserver.domain.lease.LeaseExtensions;
import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaseExtensionsRepository extends JpaRepository<LeaseExtensions, Long> {

}
