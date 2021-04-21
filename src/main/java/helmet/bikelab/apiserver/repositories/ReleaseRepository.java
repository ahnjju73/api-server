package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.lease.Releases;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReleaseRepository extends JpaRepository<Releases, Integer> {
    Releases findByReleaseId(String releaseId);
}
