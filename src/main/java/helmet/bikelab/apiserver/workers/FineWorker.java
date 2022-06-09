package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.objects.requests.AddUpdateFineRequest;
import helmet.bikelab.apiserver.repositories.BikesRepository;
import helmet.bikelab.apiserver.repositories.FinesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FineWorker extends SessService {

    private final FinesRepository finesRepository;
    private final CommonWorker commonWorker;
    private final BikesRepository bikesRepository;
    private final ClientWorker clientWorker;
    private final RiderWorker riderWorker;
    private final BikeWorker bikeWorker;
    private final AutoKey autoKey;

    public Fines getFineById(String fineId){
        return finesRepository.findByFineId(fineId);
    }

    public Fines makeNewFine(AddUpdateFineRequest request){
        Fines fines = new Fines();
        String fineId = autoKey.makeGetKey("fine");
        fines.setFineId(fineId);
        fines.setFee(request.getFee());
        fines.setPaidFee(0);
        fines.setFineNum(request.getFineNum());
        fines.setFineDate(LocalDateTime.parse(request.getFineDate()));
        fines.setFineExpireDate(LocalDateTime.parse(request.getFineExpireDate()));
        Bikes bike = bikeWorker.getBikeById(request.getBikeId());
        fines.setBikeNo(bike.getBikeNo());
        if(bePresent(request.getClientId())){
            Clients clientByClientId = clientWorker.getClientByClientId(request.getClientId());
            fines.setClientNo(clientByClientId.getClientNo());
        }
        if(bePresent(request.getRiderId())){
            Riders riderById = riderWorker.getRiderById(request.getRiderId());
            fines.setRiderNo(riderById.getRiderNo());
        }
        return fines;
    }

    public Fines setFine(AddUpdateFineRequest request, Fines fines){
        fines.setFee(request.getFee());
        fines.setPaidFee(bePresent(request.getPaidFee()) ? request.getPaidFee() : 0);
        fines.setFineNum(request.getFineNum());
        fines.setFineDate(LocalDateTime.parse(request.getFineDate()));
        fines.setFineExpireDate(LocalDateTime.parse(request.getFineExpireDate()));
        Bikes bike = bikesRepository.findByBikeId(request.getBikeId());
        if(bePresent(request.getClientId())){
            Clients clientByClientId = clientWorker.getClientByClientId(request.getClientId());
            fines.setClientNo(clientByClientId.getClientNo());
        }else{
            fines.setClientNo(null);
        }
        if(bePresent(request.getRiderId())){
            Riders riderById = riderWorker.getRiderById(request.getRiderId());
            fines.setRiderNo(riderById.getRiderNo());
        }else{
            fines.setRiderNo(null);
        }
        fines.setBikeNo(bike.getBikeNo());
        return fines;
    }

    public Fines removeAttachment(Fines fine, String uuid){
        for(ModelAttachment ma : fine.getAttachmentsList()){
            if(ma.getUuid().equals(uuid)){
                fine.getAttachmentsList().remove(ma);
            }
        }
        return fine;
    }

}
