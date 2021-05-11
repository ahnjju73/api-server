package helmet.bikelab.apiserver.repositories;


import helmet.bikelab.apiserver.domain.lease.LeaseExtras;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaseExtraRepository extends JpaRepository<LeaseExtras, Integer> {
    List<LeaseExtras> findAllByLease_LeaseId(String leaseId);
    LeaseExtras findByExtraId(String extraId);
    List<LeaseExtras> findAllByPayment_PaymentId(String paymentId);
}
