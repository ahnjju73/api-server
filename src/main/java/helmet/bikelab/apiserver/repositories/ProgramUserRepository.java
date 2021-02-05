package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.ProgramUser;
import helmet.bikelab.apiserver.domain.types.ProgramUserPK;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramUserRepository extends JpaRepository<ProgramUser, ProgramUserPK> {

    Optional<ProgramUser> findByBikeUser_UserNoAndProgram_ProgramIdAndProgram_Useable(String userNo, String programId, YesNoTypes useable);

}