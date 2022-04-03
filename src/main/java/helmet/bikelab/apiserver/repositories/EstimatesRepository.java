package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Estimates;
import helmet.bikelab.apiserver.domain.types.EstimateStatusTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface EstimatesRepository extends JpaRepository<Estimates, Integer> {
    Estimates findByEstimateId(String estimateId);
    List<Estimates> findAllByClient_ClientId(String clientId);
    List<Estimates> findAllBySettle_SettleId(String settleId);
    @Query(value = "select e from Estimates e where e.clientNo = :client_no and e.totalPrice > e.paidFee")
    List<Estimates> getUnpaidEstimates(@Param("client_no") Integer clientNo);
    Page<Estimates> findByShop_ShopIdAndReviewNotNullAndEstimateStatusType(String shopId, EstimateStatusTypes estimateStatusTypes, Pageable pageable);
    Page<Estimates> findAllByReviewNotNullAndEstimateStatusType(EstimateStatusTypes estimateStatusTypes, Pageable pageable);
}