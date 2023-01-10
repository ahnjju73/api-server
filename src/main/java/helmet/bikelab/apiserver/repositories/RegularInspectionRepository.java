package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.shops.RegularInspections;
import helmet.bikelab.apiserver.domain.types.TimeTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RegularInspectionRepository extends JpaRepository<RegularInspections, Integer> {
    Page<RegularInspections> findAllByShopNoOrderByInspectDtDesc(Integer shopNo, Pageable pageable);
    Page<RegularInspections> findAllByShopNoAndClient_ClientId(Integer shopNo, String clientId, Pageable pageable);
    Page<RegularInspections> findAllByShopNoAndGroup_GroupId(Integer shopNo, String groupId, Pageable pageable);
    Page<RegularInspections> findAllByShopNoAndInspectDtBetween(Integer shopNo, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<RegularInspections> findAllByGroupNoOrderByInspectDtDesc(Integer groupNo, Pageable pageable);
    Page<RegularInspections> findAllByGroupNoAndClient_ClientId(Integer groupNo, String clientId, Pageable pageable);
    Page<RegularInspections> findAllByGroupNoAndInspectDtBetween(Integer groupNo, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<RegularInspections> findAllByOrderByInspectDtDesc(Pageable pageable);
    Page<RegularInspections> findAllByClient_ClientId(String clientId, Pageable pageable);
    Page<RegularInspections> findAllByGroup_GroupId(String groupId, Pageable pageable);
    Page<RegularInspections> findAllByInspectDtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    RegularInspections findByInspectId(String inspectId);
    void deleteByInspectId(String inspectId);
    RegularInspections findByClient_ClientIdAndTimesAndIncludeDt(String clientId, TimeTypes timeTypes, String includeDate);

    List<RegularInspections> findAllByClientNoInAndIncludeDtBetween(List<Integer> clientsNo, String start, String end);

}
