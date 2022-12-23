package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeInfo;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeInfoRepository extends JpaRepository<BikeInfo, Integer> {
    List<BikeInfo> findAllByBikeNo(Integer bikeNo);
    BikeInfo findByInfoNo(Integer infoNo);
    BikeInfo findByBike_BikeIdAndInfoNo(String bikeId, Integer infoNo);
    void deleteByBike_BikeIdAndInfoNo(String bikeId, Integer infoNo);

}
