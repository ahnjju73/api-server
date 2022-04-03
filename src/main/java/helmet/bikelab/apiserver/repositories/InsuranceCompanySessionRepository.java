package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.ins_companies.InsuranceCompanySessions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceCompanySessionRepository extends JpaRepository<InsuranceCompanySessions, Integer> {
}
