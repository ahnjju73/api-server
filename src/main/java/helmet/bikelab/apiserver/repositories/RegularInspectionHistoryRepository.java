package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.RegularInspectionHistories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularInspectionHistoryRepository extends JpaRepository<RegularInspectionHistories, Integer> {
    RegularInspectionHistories findByRegularInspections_InspectId(String inspectId);
    void deleteByRegularInspections_InspectId(String inspectId);
}
