package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BikeLabUserRepository extends JpaRepository<BikeUser, Integer> {

    Optional<BikeUser> findByEmail(String email);

    Optional<BikeUser> findByEmailAndUserNoNot(String email, Integer userNo);

    BikeUser findByUserNoAndUserStatusTypes(Integer userNo, BikeUserStatusTypes bikeUserStatusTypes);

    @Modifying
    @Query("update BikeUser u set u.userStatusTypes = ?2 where u.userNo = ?1")
    void updateAccountStatusOfEmployeeByManager(String userNo, BikeUserStatusTypes bikeUserStatusTypes);

    void deleteByUserId(String userId);


}

