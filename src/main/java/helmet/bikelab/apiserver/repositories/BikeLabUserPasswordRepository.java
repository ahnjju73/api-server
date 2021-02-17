package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.BikeUserPassword;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BikeLabUserPasswordRepository extends JpaRepository<BikeUserPassword, Integer> {

    Optional<BikeUserPassword> findByBikeUser_EmailAndBikeUser_UserStatusTypes(String email, BikeUserStatusTypes bikeUserStatusTypes);

    Optional<BikeUserPassword> findByBikeUserNo(Integer userNo);

}
