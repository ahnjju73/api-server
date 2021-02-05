package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.Program;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, String> {

    List<Program> findByDefaultType(YesNoTypes defaultType);

}