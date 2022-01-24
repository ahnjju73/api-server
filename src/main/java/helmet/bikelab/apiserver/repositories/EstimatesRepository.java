package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.types.EstimateStatusTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EstimatesRepository extends JpaRepository<Estimates, Integer> {
    Estimates findByEstimateIdAndRiderNo(String estimateId, Integer riderNo);
    Estimates findByEstimateIdAndRiderNoAndEstimateStatusTypeNot(String estimateId, Integer riderNo, EstimateStatusTypes estimateStatusType);
    Estimates findByEstimateId(String estimateId);
    List<Estimates> findAllByClient_ClientId(String clientId);
    void deleteByEstimateId(String estimateId);
    List<Estimates> findAllByBike_BikeId(String bikeId);
    Page<Estimates> findAllByShopNo(Integer shopNo, Pageable pageable);
    Page<Estimates> findByShop_ShopIdAndReviewNotNullAndEstimateStatusType(String shopId, EstimateStatusTypes estimateStatusTypes, Pageable pageable);
}
