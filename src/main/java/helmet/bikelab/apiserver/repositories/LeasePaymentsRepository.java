package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeasePaymentsRepository extends JpaRepository<LeasePayments, Integer> {
    LeasePayments findByPaymentId(String paymentId);
    LeasePayments findFirstByLease_LeaseId(String leaseId);
    List<LeasePayments> findAllByLease_LeaseId(String leaseId);
    List<LeasePayments> findAllByLeaseNo(Integer leaseNo);
    void deleteAllByLease_LeaseId(String leaseId);
}
