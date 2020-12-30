package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUserSession;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BikeLabUserSessionRepository extends JpaRepository<BikeLabUserSession, Integer> {

    void deleteByUserNoAndSessionTypes(String userNo, UserSessionTypes userSessionTypes);

    Optional<BikeLabUserSession> findByUser_UserNoAndUser_AccountStatusTypesAndSessionTypes(String userNo, AccountStatusTypes accountStatusTypes, UserSessionTypes userSessionTypes);

}
