package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.domain.shops.Shops;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionsRepository extends JpaRepository<Sections, Integer> {
    Sections findBySectionNo(Integer sectionNo);
    List<Sections> findAllByCarModel_Code(String modelCode);
}
