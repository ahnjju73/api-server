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
    Estimates findByEstimateId(String estimateId);
    List<Estimates> findAllByClient_ClientId(String clientId);
    List<Estimates> findAllBySettle_SettleId(String settleId);
}
