package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeasePaymentsRepository extends JpaRepository<LeasePayments, Integer> {
    LeasePayments findByPaymentId(String paymentId);
}
