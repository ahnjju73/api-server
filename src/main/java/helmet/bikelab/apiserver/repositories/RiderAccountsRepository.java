package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderAccounts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderAccountsRepository extends JpaRepository<RiderAccounts, Integer> {
}
