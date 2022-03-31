package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.ins_companies.InsuranceCompanyPasswords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceCompanyPasswordRepository extends JpaRepository<InsuranceCompanyPasswords, Integer> {
    InsuranceCompanyPasswords findByCompany_CompanyId(String companyId);
}
