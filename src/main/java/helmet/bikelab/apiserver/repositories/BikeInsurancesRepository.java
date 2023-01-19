package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeInsurancesRepository extends JpaRepository<BikeInsurances, Integer> {
    BikeInsurances findByInsuranceNo(Integer insuranceNo);
    void deleteByInsuranceNo(Integer insuranceNo);
    List<BikeInsurances> findAllByBikeNoOrderByInsuranceNoDesc(Integer bikeNo);
    BikeInsurances findByBikeNoAndStockNumber(Integer bikeNo, String stockNumber);
}
