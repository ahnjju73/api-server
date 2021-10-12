package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LeasesWorker extends Workspace {

    private final LeaseRepository leaseRepository;

    public Leases getLeaseByBikeNo(Integer bikeNo){
        Leases byLeaseNo = leaseRepository.findByBikeNo(bikeNo);
        if(!bePresent(byLeaseNo)) withException("803-001");
        return byLeaseNo;
    }

    public Leases getLeaseByLeaseNo(Integer leaseNo){
        Leases byLeaseNo = leaseRepository.findByLeaseNo(leaseNo);
        if(!bePresent(byLeaseNo)) withException("803-001");
        return byLeaseNo;
    }

    public Leases getLeaseByLeaseId(String demandLeaseId){
        Leases byLeaseNo = leaseRepository.findByLeaseId(demandLeaseId);
        if(!bePresent(byLeaseNo)) withException("803-001");
        return byLeaseNo;
    }

}
