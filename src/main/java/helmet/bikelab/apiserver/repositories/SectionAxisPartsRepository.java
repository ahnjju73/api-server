package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.SectionAxis;
import helmet.bikelab.apiserver.domain.SectionAxisParts;
import helmet.bikelab.apiserver.domain.types.SectionAxisPartsPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionAxisPartsRepository extends JpaRepository<SectionAxisParts, SectionAxisPartsPK> {

    SectionAxisParts findByAxisNoAndPartsNo(Integer axisNo, Long partsNo);
    void deleteByAxisNoAndPartsNo(Integer axisNo, Long partsNo);
}
