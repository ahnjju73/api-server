package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.LeaseExpense;
import helmet.bikelab.apiserver.domain.types.ExpenseTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaseExpenseRepository extends JpaRepository<LeaseExpense, Long> {
    List<LeaseExpense> findAllByLease_LeaseIdAndExpenseTypes(String leaseId, ExpenseTypes expenseTypes);
    List<LeaseExpense> findAllByLease_LeaseId(String leaseId);
    void deleteAllByLease_LeaseId(String leaseId);
}
