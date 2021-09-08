package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.DemandLeaseStatusTypes;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.repositories.DemandLeasesRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemandLeaseWorker extends Workspace {

    private final DemandLeasesRepository demandLeasesRepository;
    private final LeaseRepository leaseRepository;

    public List<Leases> getLeasesByDemandLeaseId(String demandLeaseId, PageableRequest pageableRequest){
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
        List<Leases> allByDemandLeases_demandLeaseId = leaseRepository.findAllByDemandLeases_DemandLeaseId(demandLeaseId, pageable);
        return allByDemandLeases_demandLeaseId == null ? new ArrayList<>() : allByDemandLeases_demandLeaseId;
    }

    public DemandLeases getDemandLeaseById(String id){
        DemandLeases demandLeases = demandLeasesRepository.findByDemandLeaseId(id);
        if(!bePresent(demandLeases)) withException("851-001");
        return demandLeases;
    }

    public void updateDemandLeaseStatusByDemandLease(DemandLeases demandLeases, DemandLeaseStatusTypes demandLeaseStatusTypes){
        if(DemandLeaseStatusTypes.DENIED.equals(demandLeaseStatusTypes)){
            demandLeases.setRejectedAt(LocalDateTime.now());
        }else if(DemandLeaseStatusTypes.COMPLETED.equals(demandLeaseStatusTypes)){
            demandLeases.setCompletedAt(LocalDateTime.now());
        }
        demandLeases.setDemandLeaseStatusTypes(demandLeaseStatusTypes);
        demandLeasesRepository.save(demandLeases);
    }

}
