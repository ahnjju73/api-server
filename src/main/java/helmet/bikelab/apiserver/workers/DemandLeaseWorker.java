package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.types.DemandLeaseStatusTypes;
import helmet.bikelab.apiserver.repositories.DemandLeasesRepository;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DemandLeaseWorker extends Workspace {

    private final DemandLeasesRepository demandLeasesRepository;

    public DemandLeases getDemandLeaseById(String id){
        DemandLeases demandLeases = demandLeasesRepository.findByDemandLeaseId(id);
        if(!bePresent(demandLeases)) withException("851-001");
        return demandLeases;
    }

    public void updateDemandLeaseStatusByDemandLease(DemandLeases demandLeases, DemandLeaseStatusTypes demandLeaseStatusTypes){
        demandLeases.setDemandLeaseStatusTypes(demandLeaseStatusTypes);
        demandLeasesRepository.save(demandLeases);
    }

}
