package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.BikeUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BikeLabUserInfoRepository extends JpaRepository<BikeUserInfo, Integer> {

    Optional<BikeUserInfo> findByBikeUserNo(Integer userNo);

}
