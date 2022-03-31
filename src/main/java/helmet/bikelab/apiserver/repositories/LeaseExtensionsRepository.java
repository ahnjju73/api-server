package helmet.bikelab.apiserver.repositories;


import helmet.bikelab.apiserver.domain.lease.LeaseExtensions;
import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaseExtensionsRepository extends JpaRepository<LeaseExtensions, Long> {

    List<LeaseExtensions> findByLease_LeaseIdOrderByIdx(String leaseId);

    @Query(nativeQuery = true, value = "select count(*) from lease_extensions where lease_no = ?1")
    Integer getExtensionIndexByLeaseNo(Integer leaseNo);

}
