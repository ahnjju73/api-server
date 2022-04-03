package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeasePayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeasePaymentsRepository extends JpaRepository<LeasePayments, Integer> {

    @Query(nativeQuery = true, value = "select * from lease_payments lp where lp.lease_no = ?1 order by lp.idx desc, lp.payment_no desc limit 1")
    LeasePayments getLastIndexByLeaseNoOrderByIndexDesc(Integer leaseNo);

    LeasePayments findByPaymentId(String paymentId);
    LeasePayments findFirstByLease_LeaseId(String leaseId);
    List<LeasePayments> findAllByLease_LeaseIdAndPaymentDateLessThanEqual(String leaseId, LocalDate ld);
    List<LeasePayments> findAllByLease_LeaseId(String leaseId);
    List<LeasePayments> findAllByLease_LeaseIdOrderByIndex(String leaseId);
    List<LeasePayments> findAllByLeaseNo(Integer leaseNo);
    void deleteAllByLease_LeaseId(String leaseId);
}
