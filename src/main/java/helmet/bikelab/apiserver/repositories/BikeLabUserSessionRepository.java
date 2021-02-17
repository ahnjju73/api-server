package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUserSession;
import helmet.bikelab.apiserver.domain.types.BikeUserSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BikeLabUserSessionRepository extends JpaRepository<BikeUserSession, BikeUserSessionPK> {

    void deleteByBikeUserNoAndSessionTypes(Integer userNo, UserSessionTypes userSessionTypes);

    Optional<BikeUserSession> findByUser_UserNoAndSessionTypes(Integer userNo, UserSessionTypes userSessionTypes);

}
