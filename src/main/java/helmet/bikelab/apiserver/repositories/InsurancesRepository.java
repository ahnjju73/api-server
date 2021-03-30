package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Insurances;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsurancesRepository extends JpaRepository<Insurances, Integer> {
    public Insurances findByInsuranceId(String insuranceId);
}
