package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.SectionAxis;
import helmet.bikelab.apiserver.domain.Sections;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionAxisRepository extends JpaRepository<SectionAxis, Integer> {

    SectionAxis findBySectionNoAndAxisNo(Integer sectionNo, Integer axisNo);
    SectionAxis findByAxisNo(Integer axisNo);
    List<SectionAxis> findAllBySectionNo(Integer sectionNo);
    void deleteByAxisNo(Integer axisNo);
}
