package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUserSession;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BikeLabUserSessionRepository extends JpaRepository<BikeLabUserSession, Integer> {

    void deleteByBikeUserNoAndSessionTypes(Integer userNo, UserSessionTypes userSessionTypes);

}
