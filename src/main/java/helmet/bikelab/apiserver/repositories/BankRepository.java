package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Banks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankRepository extends JpaRepository<Banks, String> {
    Banks findByBankCode(String bankCd);
}
