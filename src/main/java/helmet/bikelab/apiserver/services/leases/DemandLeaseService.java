package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.DemandLeaseStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.DemandLeaseByIdRequest;
import helmet.bikelab.apiserver.objects.requests.RejectDemandLeaseByIdRequest;
import helmet.bikelab.apiserver.objects.responses.DemandLeaseDetailsByIdResponse;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.DemandLeaseWorker;
import helmet.bikelab.apiserver.workers.LeasesWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DemandLeaseService extends SessService {

    private final DemandLeaseWorker demandLeaseWorker;
    private final LeasesWorker leasesWorker;

    public BikeSessionRequest fetchDemandLeaseById(BikeSessionRequest request){
        DemandLeaseByIdRequest demandLeaseByIdRequest = map(request.getParam(), DemandLeaseByIdRequest.class);
        DemandLeases demandLeases = demandLeaseWorker.getDemandLeaseById(demandLeaseByIdRequest.getDemandLeaseId());
        Leases leases = leasesWorker.getLeaseByLeaseNo(demandLeases.getLeaseNo());
        DemandLeaseDetailsByIdResponse demandLeaseDetailsByIdResponse = new DemandLeaseDetailsByIdResponse();
        demandLeaseDetailsByIdResponse.setDemandLease(demandLeases);
        demandLeaseDetailsByIdResponse.setClient(demandLeases.getClient());
        if(bePresent(leases)) demandLeaseDetailsByIdResponse.setLeaseId(leases.getLeaseId());
        request.setResponse(demandLeaseDetailsByIdResponse);
        return request;
    }

    @Transactional
    public BikeSessionRequest completedDemandLeaseById(BikeSessionRequest request){
        DemandLeaseByIdRequest demandLeaseByIdRequest = map(request.getParam(), DemandLeaseByIdRequest.class);
        DemandLeases demandLeaseById = demandLeaseWorker.getDemandLeaseById(demandLeaseByIdRequest.getDemandLeaseId());
        if(!demandLeaseById.isOneOfDemandLeaseStatusType(DemandLeaseStatusTypes.PENDING)) withException("803-002");
        demandLeaseById.setRejectMessage(null);
        demandLeaseById.setCompletedAt(LocalDateTime.now());
        demandLeaseWorker.updateDemandLeaseStatusByDemandLease(demandLeaseById, DemandLeaseStatusTypes.COMPLETED);
        return request;
    }

    @Transactional
    public BikeSessionRequest denyDemandLeaseById(BikeSessionRequest request){
        RejectDemandLeaseByIdRequest demandLeaseByIdRequest = map(request.getParam(), RejectDemandLeaseByIdRequest.class);
        DemandLeases demandLeaseById = demandLeaseWorker.getDemandLeaseById(demandLeaseByIdRequest.getDemandLeaseId());
        demandLeaseById.setRejectMessage(demandLeaseByIdRequest.getRejectedMessage());
        demandLeaseWorker.updateDemandLeaseStatusByDemandLease(demandLeaseById, DemandLeaseStatusTypes.DENIED);
        return request;
    }

}
