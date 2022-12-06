package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.SampleFiles;
import helmet.bikelab.apiserver.domain.client.Clients;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleFilesRepository extends JpaRepository<SampleFiles, Integer> {

}
