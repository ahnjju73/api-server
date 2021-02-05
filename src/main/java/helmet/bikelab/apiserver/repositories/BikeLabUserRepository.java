package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUser;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BikeLabUserRepository extends JpaRepository<BikeLabUser, String> {

    Optional<BikeLabUser> findByEmail(String email);

    Optional<BikeLabUser> findByEmailAndUserNoNot(String email, Integer userNo);

    @Modifying
    @Query("update BikeLabUser u set u.userStatusTypes = ?2 where u.userNo = ?1")
    void updateAccountStatusOfEmployeeByManager(String userNo, BikeUserStatusTypes bikeUserStatusTypes);

}
