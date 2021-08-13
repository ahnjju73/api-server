package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.ComSpecialTerms;
import helmet.bikelab.apiserver.domain.Manufacturers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManufacturersRepository extends JpaRepository<Manufacturers, Integer> {

    List<Manufacturers> findAllBy();
    Manufacturers findByManufacturerNo(Integer manufacturerNo);

}
