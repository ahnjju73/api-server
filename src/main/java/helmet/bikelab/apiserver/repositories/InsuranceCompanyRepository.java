package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.ins_companies.InsuranceCompanies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceCompanyRepository extends JpaRepository<InsuranceCompanies, Integer> {
    Page<InsuranceCompanies> findAll(Pageable pageable);
    Page<InsuranceCompanies> findAllByNameContaining(String name, Pageable pageable);
    InsuranceCompanies findByCompanyId(String companyId);
}
