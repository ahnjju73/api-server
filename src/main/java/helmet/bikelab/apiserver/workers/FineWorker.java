package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.lease.Fines;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.RiderInfoDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.requests.AddUpdateFineExcelRequest;
import helmet.bikelab.apiserver.objects.requests.AddUpdateFineRequest;
import helmet.bikelab.apiserver.objects.responses.FetchFineDetailResponse;
import helmet.bikelab.apiserver.repositories.BikesRepository;
import helmet.bikelab.apiserver.repositories.ClientsRepository;
import helmet.bikelab.apiserver.repositories.FinesRepository;
import helmet.bikelab.apiserver.repositories.RiderRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FineWorker extends SessService {

    private final FinesRepository finesRepository;
    private final RiderRepository riderRepository;
    private final ClientsRepository clientsRepository;
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
        fines.setFineType(request.getFineType());
        fines.setFineDate(LocalDateTime.parse(request.getFineDate()));
        fines.setFineExpireDate(LocalDateTime.parse(request.getFineExpireDate()));
        fines.setFineLocation(request.getFineLocation());
        fines.setFineOffice(request.getFineOffice());
        fines.setViolationReason(request.getViolationReason());
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
        fines.setFineType(request.getFineType());
        fines.setFineDate(LocalDateTime.parse(request.getFineDate()));
        fines.setFineExpireDate(LocalDateTime.parse(request.getFineExpireDate()));
        fines.setFineLocation(request.getFineLocation());
        fines.setFineOffice(request.getFineOffice());
        fines.setViolationReason(request.getViolationReason());
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
        for (int i = 0; i < fine.getAttachmentsList().size(); i++) {
            if(fine.getAttachmentsList().get(i).getUuid().equals(uuid))
                fine.getAttachmentsList().remove(i--);
        }
        return fine;
    }

    public FetchFineDetailResponse getFineInfo(Fines fine){
        FetchFineDetailResponse fetchFineDetailResponse = new FetchFineDetailResponse();
        fetchFineDetailResponse.setFineInfo(fine);
        BikeDto bikeDto = new BikeDto();
        Bikes bikeByNo = bikeWorker.getBikeByNo(fine.getBikeNo());
        bikeDto.setBikeId(bikeByNo.getBikeId());
        bikeDto.setBikeNum(bikeByNo.getCarNum());
        bikeDto.setVimNum(bikeByNo.getVimNum());
        bikeDto.setBikeModel(bikeByNo.getCarModel().getModel());
        fetchFineDetailResponse.setBike(bikeDto);
        if(bePresent(fine.getRiderNo())){
            Riders byRiderNo = riderRepository.findByRiderNo(fine.getRiderNo());
            RiderInfoDto riderInfoDto = new RiderInfoDto();
            riderInfoDto.setRiderId(byRiderNo.getRiderId());
            riderInfoDto.setRiderName(byRiderNo.getRiderInfo().getName());
            riderInfoDto.setRiderStatus(byRiderNo.getStatus().getRiderStatusType());
            fetchFineDetailResponse.setRider(riderInfoDto);
        }
        if (bePresent(fine.getClientNo())){
            Clients clients = clientsRepository.findById(fine.getClientNo()).get();
            ClientDto clientDto = new ClientDto();
            clientDto.setClientId(clients.getClientId());
            clientDto.setClientName(clients.getClientInfo().getName());
            fetchFineDetailResponse.setClient(clientDto);
        }
        return fetchFineDetailResponse;
    }

    public Fines setFine(AddUpdateFineRequest request){
        Fines fines = new Fines();
        String fineId = autoKey.makeGetKey("fine");
        fines.setFineId(fineId);
        fines.setFee(request.getFee());
        fines.setPaidFee(0);
        fines.setFineType("과태료");
        fines.setFineDate(LocalDateTime.parse(request.getFineDate()));
        fines.setFineExpireDate(LocalDateTime.parse(request.getFineExpireDate()));
        fines.setFineLocation(request.getFineLocation());
        fines.setFineOffice(request.getFineOffice());
        fines.setViolationReason(request.getViolationReason());
        Bikes bike = bikesRepository.findByCarNum(request.getBikeNum());
        fines.setBikeNo(bike.getBikeNo());
        Clients client = clientWorker.getClientByBike(bike);
        if(bePresent(client)){
            fines.setClientNo(client.getClientNo());
        }
        if(bePresent(bike.getRiderNo())){
            fines.setRiderNo(bike.getRiderNo());
        }
        return fines;
    }

}
