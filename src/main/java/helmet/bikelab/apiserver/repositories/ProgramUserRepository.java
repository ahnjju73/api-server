package helmet.bikelab.apiserver.repositories;

import helmet.bikelab.apiserver.domain.bikelab.ProgramUser;
import helmet.bikelab.apiserver.domain.types.ProgramUserPK;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProgramUserRepository extends JpaRepository<ProgramUser, ProgramUserPK> {

    Optional<ProgramUser> findByBikeUserNoAndProgram_ProgramIdAndProgram_Usable(Integer userNo, String programId, YesNoTypes usable);

    ProgramUser findByProgram_ProgramIdAndProgram_UsableAndBikeUserNo(String programId, YesNoTypes usable, Integer userNo);

    ProgramUser findByProgramNoAndBikeUserNo(Integer programNo, Integer bikeUserNo);

}