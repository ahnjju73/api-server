package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeLabUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BikeLabUserInfoRepository extends JpaRepository<BikeLabUserInfo, String> {

    Optional<BikeLabUserInfo> findByBikeUserNo(Integer userNo);

}
