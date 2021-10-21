package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.riders.RiderVerified;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.RiderVerifiedRequestTypes;
import helmet.bikelab.apiserver.domain.types.RiderVerifiedTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiderVerifiedRepository extends JpaRepository<RiderVerified, Integer> {

    void deleteAllByRiderNoAndRequestType(Integer riderNo, RiderVerifiedRequestTypes riderVerifiedType);
    List<RiderVerified> findAllByRiderNoAndRequestType(Integer riderNo, RiderVerifiedRequestTypes riderVerifiedType);
}
