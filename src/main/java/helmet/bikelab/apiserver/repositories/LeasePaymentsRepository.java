package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeasePaymentsRepository extends JpaRepository<LeasePayments, Integer> {
    LeasePayments findByPaymentId(String paymentId);
    List<LeasePayments> findAllByLease_LeaseId(String leaseId);

}
