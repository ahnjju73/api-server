package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bike.BikeAttachments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BikeAttachmentRepository extends JpaRepository<BikeAttachments, Integer> {
    List<BikeAttachments> findAllByBike_BikeId(String bikeId);
    BikeAttachments findByBikeFileInfoNo(Integer bileFileInfoNo);

}
