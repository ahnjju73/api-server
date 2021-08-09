package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.ComSpecialTerms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComSpecialTermsRepository extends JpaRepository<ComSpecialTerms, String> {

    List<ComSpecialTerms> findAllByUsable(Boolean usable);

}
