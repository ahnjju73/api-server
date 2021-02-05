package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeLabUserPassword;
import helmet.bikelab.apiserver.domain.types.AccountStatusTypes;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BikeLabUserPasswordRepository extends JpaRepository<BikeLabUserPassword, BikeLabUser> {

    Optional<BikeLabUserPassword> findByBikeUser_EmailAndBikeUser_UserStatusTypes(String email, BikeUserStatusTypes bikeUserStatusTypes);

    Optional<BikeLabUserPassword> findByBikeUserNo(Integer userNo);

}
