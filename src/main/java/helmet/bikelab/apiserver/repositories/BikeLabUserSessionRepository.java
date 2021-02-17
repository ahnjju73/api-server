package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUserSession;
import helmet.bikelab.apiserver.domain.types.BikeUserSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BikeLabUserSessionRepository extends JpaRepository<BikeUserSession, BikeUserSessionPK> {

    void deleteByBikeUserNoAndSessionTypes(Integer userNo, UserSessionTypes userSessionTypes);

}
